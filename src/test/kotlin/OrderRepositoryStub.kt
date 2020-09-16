import java.time.LocalDate
import java.util.*

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

    override fun listOrders(from: LocalDate, to: LocalDate): List<Order> {
        return db.values.filter { (it.date.isEqual(from) || it.date.isEqual(to)) || (it.date.isAfter(from) && it.date.isBefore(to)) }
    }

}
