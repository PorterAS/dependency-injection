import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import java.time.LocalDate

class BusinessServiceTest {

    @Test
    fun testShouldCalculateAverage() {
        val service = BusinessServiceImpl(BusinessRepositoryStub())

        service.addOrder(Order("1", LocalDate.of(2018, 1, 1), emptyList()))

        val average = service.getAverageOrders(LocalDate.of(2018, 1, 1), LocalDate.of(2018, 1, 2))
        assertThat(average, equalTo(0.5))
    }


}
