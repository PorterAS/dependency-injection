import java.time.LocalDate
import java.util.*

interface OrderRepository {
    fun getOrder(identifier: UUID): Order
    fun addOrder(order: Order): UUID
    fun listOrders(from: LocalDate, to: LocalDate): List<Order>
}

class OrderRepositoryImpl : OrderRepository {
    override fun getOrder(identifier: UUID): Order {
        TODO("Not yet implemented")
    }

    override fun addOrder(order: Order): UUID {
        TODO("Not yet implemented")
    }

    override fun listOrders(from: LocalDate, to: LocalDate): List<Order> {
        TODO("Not yet implemented")
    }
}
