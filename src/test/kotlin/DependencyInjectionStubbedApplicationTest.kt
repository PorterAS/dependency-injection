import com.mashape.unirest.http.Unirest
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.containsSubstring
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * This class shows almost the same ting as the other one. But it has it's repository stubbed,
 * so you can write fast efficient tests that avoids hitting some shared state or slow database.
 */
class DependencyInjectionStubbedApplicationTest {

    companion object {
        private lateinit var serverUrl: String
        private lateinit var app: DependencyInjectionApplication
        private val orderRepo = OrderRepositoryStub()

        @BeforeAll
        @JvmStatic
        fun setupServer() {
            app = DependencyInjectionApplicationContext(
                    loadConfig(),
                    orderRepository = orderRepo
            ).create()
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

}
