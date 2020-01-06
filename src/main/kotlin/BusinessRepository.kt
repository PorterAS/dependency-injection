import java.time.LocalDate
import java.time.Period

interface BusinessRepository {
    fun getData(identifier: String): String

}

class BusinessRepositoryImpl : BusinessRepository {
    override fun getData(identifier: String): String {
        return "Hello World!"
    }

}

interface BusinessService {

    fun getData(identifier: String): String
    fun addOrder(order: Order)
    fun getAverageOrders(from: LocalDate, to: LocalDate): Double

}

class BusinessServiceImpl(private val businessRepository: BusinessRepository) : BusinessService {
    private val db = HashMap<String, Order>();

    override fun getAverageOrders(from: LocalDate, to: LocalDate): Double {
        val orders = this.db.values.filter { (it.date.isEqual(from) || it.date.isEqual(to)) || (it.date.isAfter(from) && it.date.isBefore(to)) }
        return orders.size.toDouble() / Period.between(from, to.plusDays(1)).days
    }

    override fun addOrder(order: Order) {
        db[order.id] = order
    }

    override fun getData(identifier: String): String {
        return businessRepository.getData(identifier)
    }

}
