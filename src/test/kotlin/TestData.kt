import java.time.LocalDate
import java.util.*

fun Order.Companion.validOrder(deviations: Int = 0): Order {
    return Order(
            id = UUID.randomUUID(),
            date = LocalDate.now(),
            comment = "Test order",
            deviations = (1..deviations).toList().map { Deviation("Deviation number $it") }
    )
}

fun Order.Companion.invalidOrder(): Order {
    return validOrder(deviations = 1)
}
