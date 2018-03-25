import com.mashape.unirest.http.Unirest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.core.Is
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class DependencyInjectionApplicationTest {

    companion object {
        private lateinit var serverUrl: String
        private lateinit var app: DependencyInjectionApplication

        @BeforeAll
        @JvmStatic
        fun setupServer() {
            app = DependencyInjectionApplicationContext(loadConfig(true)).create()
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
        assertThat(response.body, Is(equalTo("Hello World!")))
    }

}