import com.mashape.unirest.http.Unirest
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.containsSubstring
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.startsWith
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * This class tests the entire application with injected dependencies. That might be a bad idea, but
 * it really depends on your domain and what you need to be certain of. At least this setup shows
 * that it does not have to be slow. Any bootup issues are usually the frameworks and checks and scans
 * they do when starting up.
 */
class DependencyInjectionApplicationTest {

    companion object {
        private lateinit var serverUrl: String
        private lateinit var app: DependencyInjectionApplication
        private lateinit var orderRepo: OrderRepository

        @BeforeAll
        @JvmStatic
        fun setupServer() {
            app = DependencyInjectionApplicationContext(loadConfig()).create()
            orderRepo = app.orderRepository
            serverUrl = app.start()
        }

        @AfterAll
        @JvmStatic
        fun teardownServer() {
            app.stop()
        }
    }

    @Test
    fun testThatTheApplicationIsAcceptingRequestsAtHelloWorld() {
        val response = Unirest.get("$serverUrl/helloworld").asString()
        assertThat(response.body, equalTo("Hello world"))
    }

    @Test
    fun testThatOrderCanBeFetched() {
        val orderId = orderRepo.addOrder(Order.validOrder().copy(comment = "ApplicationTestOrder"))
        assertThat(Unirest.get("$serverUrl/order/$orderId").asString().body, containsSubstring("ApplicationTestOrder"))
    }

    @Test
    fun testThatAllOrdersCanBeFetched() {
        // This is a pretty weak test, but tests that the endpoint responds. :)
        val orderId = orderRepo.addOrder(Order.validOrder().copy(comment = "ApplicationTestOrder"))
        val fetchedBody = Unirest.get("$serverUrl/order/list").asString().body
        assertThat(fetchedBody, containsSubstring(orderId.toString()))
        assertThat(fetchedBody, startsWith("["))
    }
}
