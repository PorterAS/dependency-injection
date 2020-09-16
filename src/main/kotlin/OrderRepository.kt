import org.jdbi.v3.core.Jdbi
import java.lang.RuntimeException
import java.time.LocalDate
import java.util.*

interface OrderRepository {
    fun getOrder(identifier: UUID): Order
    fun addOrder(order: Order): UUID
    fun listOrders(from: LocalDate, to: LocalDate): List<Order>
}

class OrderRepositoryImpl(private val jdbi: Jdbi) : OrderRepository {
    override fun getOrder(identifier: UUID): Order {
        return jdbi.inTransaction<Order, RuntimeException> {
            it.createQuery("SELECT * FROM orders WHERE id = :id")
                    .bind("id", identifier)
                    .map { rs, _ ->
                        Order(
                                UUID.fromString(rs.getString("id")),
                                rs.getDate("date").toLocalDate(),
                                rs.getString("comment")
                        )
                    }.single()
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

    override fun listOrders(from: LocalDate, to: LocalDate): List<Order> {
        TODO("Not yet implemented")
    }
}
