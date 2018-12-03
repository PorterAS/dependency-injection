import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsEqual
import org.junit.jupiter.api.Test
import java.time.LocalTime

class WritingTestDslTest {

    @Test
    fun testThatDeliveryTimesIsForTheNextWholeHour() {
        testConfig {
            time = "09:34"
        } whenAvailabilityRequestIsMade {
            fromAddress = "Keysers Gate 13, 0186 Oslo, Norway"
            toAddress = "Bjarertveien 17, 0579 Oslo, Norway"
        } assertValidDeliveryTimes {
            Pair(LocalTime.of(10, 0), LocalTime.of(13, 0))
        }
    }
    
}

fun testConfig(setupFunction: TestSetup.() -> Unit): TestSetup {
    return TestSetup().apply(setupFunction)
}


class TestSetup {
    lateinit var time: String

    infix fun whenAvailabilityRequestIsMade(function: TestRequestInfo.() -> Unit): TestRequestInfo {
        return TestRequestInfo(this).apply(function)
    }

}

class TestRequestInfo(private val testSetup: TestSetup) {
    lateinit var fromAddress: String
    lateinit var toAddress: String

    infix fun assertValidDeliveryTimes(function: () -> Pair<LocalTime, LocalTime>) {
        MatcherAssert.assertThat(generateDeliveryTimesForRequest(testSetup.time, fromAddress, toAddress), IsEqual(function()))
    }

}


/**
 * This should really be the production code that you're testing
 */
fun generateDeliveryTimesForRequest(time: String, fromAddress: String, toAddress: String): Pair<LocalTime, LocalTime> {
    return LocalTime.of(10, 0) to LocalTime.of(13, 0)
}
