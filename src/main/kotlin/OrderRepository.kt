import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.lang.RuntimeException
import java.sql.ResultSet
import java.time.LocalDate
import java.util.*
import java.util.stream.Stream
import kotlin.streams.toList

interface OrderRepository {
    fun getOrder(identifier: UUID): Order
    fun addOrder(order: Order): UUID
    fun listOrders(handle: Handle?, from: LocalDate, to: LocalDate): Stream<Order>
    fun listOrders(from: LocalDate, to: LocalDate): List<Order>
}

class OrderRepositoryImpl(private val jdbi: Jdbi) : OrderRepository {
    override fun getOrder(identifier: UUID): Order {
        return jdbi.inTransaction<Order, RuntimeException> {
            it.createQuery("SELECT * FROM orders WHERE id = :id")
                    .bind("id", identifier)
                    .map(OrderMapper())
                    .single()
        }
    }

    override fun addOrder(order: Order): UUID {
        return jdbi.inTransaction<UUID, RuntimeException> {
            val newId = UUID.randomUUID()
            it.createUpdate("INSERT INTO orders(id, date, comment) VALUES(:id, :date, :comment)")
                    .bind("id", newId)
                    .bind("date", order.date)
                    .bind("comment", order.comment)
                    .execute()
            newId
        }
    }

    override fun listOrders(
            // Needs to be open until last element is read from the stream. Since this method does not know
            // when that is, we open and handle at a higher level. In some ways this can be compared to
            // the good old open session in view pattern.
            handle: Handle?,
            from: LocalDate,
            to: LocalDate
    ): Stream<Order> {
        // TODO Did not limit on dates here yet
        return handle!!.createQuery("SELECT * FROM orders")
                .setFetchSize(30) // Important to make it fetch in chunks
                .map(OrderMapper())
                .stream()
    }

    override fun listOrders(from: LocalDate, to: LocalDate): List<Order> {
        return jdbi.inTransaction<List<Order>, RuntimeException> {
            listOrders(it, from, to).toList()
        }
    }

    private class OrderMapper : RowMapper<Order> {
        override fun map(rs: ResultSet, ctx: StatementContext?): Order {
            return Order(
                    UUID.fromString(rs.getString("id")),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("comment")
            )
        }

    }
}
