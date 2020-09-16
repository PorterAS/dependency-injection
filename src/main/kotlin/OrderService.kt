import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import java.lang.RuntimeException
import java.time.LocalDate
import java.time.Period
import java.util.*
import java.util.stream.Stream
import kotlin.streams.toList

class OrderService(
        // This is made public for testing. Generally not wanted, but we don't enforce usage of this programatically. In our view it just creates too much complexity vs the value.
        val orderRepository: OrderRepository
) {

    fun getAverageOrders(from: LocalDate, to: LocalDate): Double {
        val orders = orderRepository.listOrders(from, to)
        return orders.size.toDouble() / Period.between(from, to.plusDays(1)).days
    }

    fun addOrder(order: Order) {
        orderRepository.addOrder(order)
    }

    fun getOrder(identifier: UUID): Order {
        return orderRepository.getOrder(identifier)
    }

    fun listOrders(handle: Handle, from: LocalDate, to: LocalDate): Stream<Order> {
        return orderRepository.listOrders(handle, from, to)
    }

}
