import java.time.LocalDate
import java.time.Period
import java.util.*

interface OrderService {

    fun getOrder(identifier: UUID): Order
    fun addOrder(order: Order)
    fun getAverageOrders(from: LocalDate, to: LocalDate): Double

}

class OrderServiceImpl(private val orderRepository: OrderRepository) : OrderService {
    override fun getAverageOrders(from: LocalDate, to: LocalDate): Double {
        val orders = orderRepository.listOrders(from, to)
        return orders.size.toDouble() / Period.between(from, to.plusDays(1)).days
    }

    override fun addOrder(order: Order) {
        orderRepository.addOrder(order)
    }

    override fun getOrder(identifier: UUID): Order {
        return orderRepository.getOrder(identifier)
    }

}
