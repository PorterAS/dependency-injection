package languageexamples

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
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

    @Test
    fun testNullabilityValAndExtensionMethods() {
        val value: String = "Hello"
        // value = "Hello2" // Does not compile
        var mutableValue = "Hello"
        mutableValue = "Hello2" // Var so mutable

        val nullableValue: String? = null
        // val slice: String = nullableValue.slice(4..50) // Does not compile
        // val slice: String = nullableValue!!.slice(4..50) // RuntimeException

        val slice = value.slice(1..2) // Extension method to String from Kotlin
        assertThat(slice, equalTo("el"))
    }

    @Test
    fun testDataClassesAndConstruction() {
        data class Address(val streetName: String, val streetNumber: String, val postCode: String, val country: String)
        data class Person(val name: String, val address: Address, val phoneNumber: String)

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
        assertTrue(me.address == someone.address)
        assertTrue(me.address === someone.address)
        assertFalse(me.address == someoneAtDifferentAddress.address) // Equality
        assertFalse(me.address === someoneAtDifferentAddress.address)
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
        val totalGamesPlayed = stats.sumByLong { it.gamesPlayed } // Kotlin doesn't have a sumBy for Longs, see above for implementation

        assertThat(totalGamesPlayed, equalTo(42L))
    }

    @Test
    fun testTransforms() {
        data class FamilyStatus(val name: String, val children: Int)

        val myNumber: Int = "1234".let { it.toInt() }
        val myList = listOf(FamilyStatus("John", 3), FamilyStatus("Sam", 7), FamilyStatus("Anna", 2))
        assertThat(
                myList
                        .sortedBy { it.name }
                        .last { it.children > 5 }
                        .name,
                equalTo("Sam")
        )
        assertThat(myList.single { it.name == "John" }, equalTo(FamilyStatus("John", 3)))

        val myListOfJustNames: List<String> = myList.map { it.name }
        val myMapOfNames: Map<String, FamilyStatus> = myList.associateBy { it.name }
        val myMapOfNamesAndJustNumbers: Map<String, Int> = myList.map { it.name to it.children }.toMap()
    }
}


