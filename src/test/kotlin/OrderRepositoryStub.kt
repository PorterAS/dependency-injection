import org.jdbi.v3.core.Handle
import java.time.LocalDate
import java.util.*
import java.util.stream.Stream
import kotlin.streams.toList

class OrderRepositoryStub : OrderRepository {
    private val db = mutableMapOf<UUID, Order>()

    override fun getOrder(identifier: UUID): Order {
        return db[identifier]!!
    }

    override fun addOrder(order: Order): UUID {
        return UUID.randomUUID().also {
            db[it] = order.copy(id = it)
        }
    }

    override fun listOrders(handle: Handle?, from: LocalDate, to: LocalDate): Stream<Order> {
        return db.values.stream().filter { (it.date.isEqual(from) || it.date.isEqual(to)) || (it.date.isAfter(from) && it.date.isBefore(to)) }
    }

    override fun listOrders(from: LocalDate, to: LocalDate): List<Order> {
        return listOrders(null, from, to).toList()
    }

}
