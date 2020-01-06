import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class OrderDomainTest {

    @Test
    fun shouldBeAbleToApproveOrderWithNoDeviations() {
        assertThat(Order.validOrder().canBeApproved(), equalTo(true))
    }

    @Test
    fun shouldNotApproveOrderWithDeviations() {
        val order = Order.validOrder().copy(deviations = listOf(Deviation("Could not find destination")))
        assertThat(order.canBeApproved(), equalTo(false))
    }

    @Test
    fun shouldNotApproveOrderWithDeviationsWithGeneratorMethod() {
        val order = Order.validOrder(deviations = 1)
        assertThat(order.canBeApproved(), equalTo(false))
    }

}
