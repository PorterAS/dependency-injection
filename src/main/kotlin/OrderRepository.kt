import java.time.LocalDate
import java.util.*

interface OrderRepository {
    fun getOrder(identifier: UUID): String
    fun addOrder(order: Order)
    fun listOrders(from: LocalDate, to: LocalDate): List<Order>
}

class OrderRepositoryImpl : OrderRepository {
    override fun getOrder(identifier: UUID): String {
        return "Hello World!"
    }

    override fun addOrder(order: Order) {
        TODO("Not yet implemented")
    }

    override fun listOrders(from: LocalDate, to: LocalDate): List<Order> {
        TODO("Not yet implemented")
    }
}
