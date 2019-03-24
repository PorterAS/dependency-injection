package languageexamples

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.vavr.control.Option
import io.vavr.control.Try
import io.vavr.control.Validation
import io.vavr.kotlin.Try
import io.vavr.kotlin.option
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Examples for Kotlin for Java developers
 *
 * Typing added many places for clarity
 *
 */
class LanguageExamplesTest {
    data class Address(val streetName: String, val streetNumber: String, val postCode: String, val country: String)
    data class Person(val name: String, val address: Address, val phoneNumber: String)

    @Test
    fun testNullabilityValAndExtensionMethods() {
        val value: String = "Hello"
        // value = "Hello2" // Does not compile
        var mutableValue = "Hello"
        mutableValue = "Hello2" // Var so mutable

        val nullableValue: String? = null
        // val slice: String = nullableValue.slice(4..50) // Does not compile
        // val slice: String = nullableValue!!.slice(4..50) // RuntimeException

        val slice = value.slice(1..3) // Extension method to String from Kotlin
        assertThat(slice, equalTo("ell"))
    }

    @Test
    fun testCollectionMethods() {
        val myList = listOf("one", "two", "three")

        assertThat(myList.filter { it.startsWith("t") }.size, equalTo(2))
        assertThat(myList.first { listItem -> listItem.startsWith("t") }, equalTo("two"))

        val myMap = mapOf<String, Long>(
                "Hello" to 2L,
                "Yes" to 100L
        )

        assertThat(myMap["Yes"], equalTo(100L))
    }


    @Test
    fun testDataClassesAndConstruction() {
        val me = Person(
                "Anders Sveen",
                Address("Mystreet", "64", "0547", "Norway"),
                "99999999"
        )

        val someone = me.copy(name = "Someone Sveen")
        val someoneAtDifferentAddress = someone.copy(
                address = someone.address.copy(
                        postCode = "1111"
                )
        )

        assertThat(me.name, equalTo("Anders Sveen"))
        assertThat(someone.name, equalTo("Someone Sveen"))
        assertThat(someoneAtDifferentAddress.address.postCode, equalTo("1111"))

        assertThat(me.address, equalTo(someone.address)) // Different objects, equals through data class
        assertTrue(me.address == someone.address) // Equality
        assertTrue(me.address === someone.address) // Memory equality
        assertFalse(me.address == someoneAtDifferentAddress.address) // Equality
        assertFalse(me.address === someoneAtDifferentAddress.address) // Memory equality
    }

    @Test
    fun testExtensionFunctions() {
        fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
            var sum = 0L
            for (element in this) {
                sum += selector(element)
            }
            return sum
        }

        data class PlayerStats(val name: String, val gamesPlayed: Long)

        val stats = listOf(
                PlayerStats("Anders", 13),
                PlayerStats("Martin", 9),
                PlayerStats("Kristian", 20)
        )
        val totalGamesPlayedWithOut = stats.map { it.gamesPlayed }.sum()
        val totalGamesPlayed = stats.sumByLong { it.gamesPlayed } // Kotlin doesn't have a sumBy for Longs, see above for implementation

        assertThat(totalGamesPlayed, equalTo(42L))
        assertThat(totalGamesPlayedWithOut, equalTo(42L))
    }

    @Test
    fun testTransforms() {
        data class FamilyStatus(val name: String, val children: Int)

        val myNumber: Int = "1234".let { it.toInt() }
        val myList = listOf(
                FamilyStatus("John", 3),
                FamilyStatus("Sam", 7),
                FamilyStatus("Anna", 2)
        )
        assertThat(
                myList.sortedBy { it.name }.last { it.children > 5 }.name,
                equalTo("Sam")
        )
        assertThat(myList.single { it.children > 5 }, equalTo(FamilyStatus("Sam", 7)))

        val myMapOfNamesAndJustNumbers: Map<String, Int> = myList.map { it.name to it.children }.toMap()
        val myListOfJustNames: List<String> = myList.map { it.name }

        val myMapOfNames: Map<String, FamilyStatus> = myList.associateBy { it.name }
        assertThat(myMapOfNames["John"], equalTo(FamilyStatus("John", 3)))
    }

    @Test
    fun testOptions() {
        fun fetchAddress(addressId: String): Try<Address> {
            return Try {
                when (addressId) {
                    "1" -> Address("Streetname", "1", "8723", "Norway")
                    else -> throw IllegalStateException("No address")
                }
            }
        }

        val addressFetch = fetchAddress("1")
        addressFetch.map {
            // assign to domain
        }.onFailure { exception ->
            println("Could not find address (Try) $exception")
        }

        addressFetch.toOption().map {
            // assign to domain
        }.onEmpty {
            println("Could not find address (Option)")
        }
    }

    @Test
    fun testReturnValuesOfBlocks() {
        val enteredAddress: String? = null

        val addressForStorage = if (enteredAddress != null) {
            enteredAddress
        } else {
            "Karl Johan 1, Oslo, Norway"
        }
        assertThat(addressForStorage, equalTo("Karl Johan 1, Oslo, Norway"))

        val addressForStorage2 = enteredAddress.option().getOrElse { "Karl Johan 1, Oslo, Norway" }

        val displayTitle = when (addressForStorage) {
            "Karl Johan 1, Oslo, Norway" -> "Slottet"
            else -> enteredAddress
        }
        assertThat(displayTitle, equalTo("Slottet"))
    }

    enum class Role { ADMIN_ROLE }
    data class ValidationError(val message: String, val path: String)

    @Test
    fun testValidationsChaining() {
        val username = "fullaccess"
        val accessNames = listOf(Role.ADMIN_ROLE)

        // First assign role to user
        val creationResult = if (username == "fullaccess") {
            Validation.valid<ValidationError, Role>(Role.ADMIN_ROLE)
        } else {
            Validation.invalid(ValidationError("No access", "user"))
        }.flatMap { role ->
            if (accessNames.contains(role)) {
                // val newId = orderService.createNew(json)
                Validation.valid<ValidationError, String>("newStoredId")
            } else {
                Validation.invalid(ValidationError("Not allowed", "user.role"))
            }
        }

        creationResult.map {
            // Status 200
            // Convert to JSON
        }.mapError {
            // Status 422
            // Convert to JSON
        }


    }
}

