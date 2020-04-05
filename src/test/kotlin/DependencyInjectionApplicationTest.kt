import com.mashape.unirest.http.Unirest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.core.Is
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

        @BeforeAll
        @JvmStatic
        fun setupServer() {
            app = DependencyInjectionApplicationContext(loadConfig()).create()
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
