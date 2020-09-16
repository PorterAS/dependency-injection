import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import java.time.LocalDate

class OrderServiceTest {

    @Test
    fun testShouldCalculateAverage() {
        val service = OrderService(OrderRepositoryStub())

        service.addOrder(Order(comment = "Test", date = LocalDate.of(2018, 1, 1)))

        val average = service.getAverageOrders(LocalDate.of(2018, 1, 1), LocalDate.of(2018, 1, 2))
        assertThat(average, equalTo(0.5))
    }


}
