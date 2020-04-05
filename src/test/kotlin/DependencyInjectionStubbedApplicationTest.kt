import com.mashape.unirest.http.Unirest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.core.Is
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

        @BeforeAll
        @JvmStatic
        fun setupServer() {
            app = DependencyInjectionApplicationContext(
                    loadConfig(),
                    businessRepository = BusinessRepositoryStub()
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
    fun testThatTheApplicationIsAcceptingRequestsAtTheBaseUrl() {
        val response = Unirest.get(serverUrl).asString()
        assertThat(response.body, Is(equalTo("Hello World Stubbed!")))
    }

}
