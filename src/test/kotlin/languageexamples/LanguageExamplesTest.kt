package languageexamples

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.containsSubstring
import com.natpryce.hamkrest.equalTo
import io.vavr.control.Try
import io.vavr.kotlin.Try
import io.vavr.kotlin.option
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Examples for Kotlin for Java developers
 *
 * Typing added many places for clarity
 *
 */
class LanguageExamplesTest {
    data class Person(
            val name: String,
            val address: Address,
            val phoneNumber: String
    ) {
        companion object {} // Empty block to enable extensions in test

        val livesInNorway by lazy { address.country == "Norway" }
        val norwegianPhoneNumber: Boolean
            get() = phoneNumber.startsWith("+47")
    }

    data class Address(
            val streetName: String,
            val streetNumber: String,
            val postCode: String,
            val country: String
    )

    @Test
    fun testNullabilityVal() {
        val value: String = "Hello"
        // value = "Hello2" // Does not compile
        var mutableValue = "Hello"
        mutableValue = "Hello2" // Var, so mutable

        val nullableValue: String? = null
        // val slice: String = nullableValue.slice(4..50) // Does not compile
        // val slice: String = nullableValue!!.slice(4..50) // RuntimeException
        val nullSlice: String? = nullableValue?.slice(4..50)
        assertThat(nullSlice, equalTo<String?>(null))
    }

    private fun exampleMethodSimple(myString: String = "stupidstring"): String {
        return myString.toUpperCase()
    }

    private fun exampleMethod(
            myString: String,
            processor: (input: String) -> String = String::toUpperCase
    ): String {
        // Do something before
        return processor(myString)
        // Do something after?
    }

    private fun exampleMethod2(
            myString: String,
            processor: (input: String) -> String = {
                it.toUpperCase()
            }
    ): String {
        // Do something before
        return processor(myString)
        // Do something after?
    }

    @Test
    fun testMethodDefinition() {
        val testString = "ThisIsAMixedCaseString"

        assertThat(exampleMethodSimple(testString), equalTo("THISISAMIXEDCASESTRING"))
        assertThat(exampleMethod(testString), equalTo("THISISAMIXEDCASESTRING"))
        assertThat(exampleMethod(
                testString,
                { it.toLowerCase() }
        ), equalTo("thisisamixedcasestring"))

        val resultWithFunctionLast = exampleMethod(testString) { conversionString ->
            conversionString.toLowerCase()
        }
        assertThat(resultWithFunctionLast, equalTo("thisisamixedcasestring"))
    }

    @Test
    fun testStringInterpolation() {
        val value = "World"
        val oneLineString = "Hello $value"
        assertThat(oneLineString, equalTo("Hello World"))

        val multiLineString = """
            {
                "message": "$oneLineString",
                "length": ${oneLineString.length},
                "long":
                "${if (oneLineString.length > 100) "true" else "false"}"
            }
        """.trimIndent()
        assertThat(multiLineString,
                containsSubstring(""" "message": "Hello World" """.trim())
        )
    }

    @Test
    fun testCollectionMethods() {
        val myList = listOf("one", "two", "three")

        assertThat(
                myList.filter({ it.startsWith("t") }).size,
                equalTo(2)
        )
        assertThat(
                myList.filter() { it.contains("o") }.size,
                equalTo(2)
        )
        assertThat(
                myList.first { listItem -> listItem.startsWith("t") },
                equalTo("two")
        )

        val myMap = mapOf<String, Long>(
                "Hello" to 2L,
                "Yes" to 100L,
                "No" to 1000L
        )

        assertThat(myMap["Yes"], equalTo(100L))

        val myMapPair = mapOf<String, Long>(
                "Hello" to 2L,
                "Yes".to(100L),
                Pair("No", 1000L)
        )

        assertThat(myMapPair["Yes"], equalTo(100L))

    }

    @Test
    fun testDestructuring() {
        val myPair = "Anders" to "Is present"
        val (name, text) = myPair

        assertThat(name, equalTo("Anders"))
        assertThat(text, equalTo("Is present"))
    }

    @Test
    fun testOneLineIf() {
        val result = if (true) "True" else "False"
        assertThat(result, equalTo("True"))
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

        val displayTitle = when (enteredAddress) {
            "Stortinget" -> "Karl Johan 22, 0026 Oslo"
            else -> "Karl Johan 1, Oslo, Norway"
        }
        assertThat(displayTitle, equalTo("Karl Johan 1, Oslo, Norway"))

        val addressForStorage2 = enteredAddress.option().getOrElse { "Karl Johan 1, Oslo, Norway" }
        assertThat(addressForStorage2, equalTo("Karl Johan 1, Oslo, Norway"))

        val addressForStorage3 = enteredAddress ?: "Karl Johan 1, Oslo, Norway"
        assertThat(addressForStorage3, equalTo("Karl Johan 1, Oslo, Norway"))
    }

    @Test
    fun testDataClassesAndConstruction() {
        val me = Person(
                "Anders Sveen",
                Address("Mystreet", "64", "0547", "Norway"),
                "99999999"
        )

        val secondPerson = me.copy(name = "Someone Sveen", address = me.address.copy())
        val secondPersonDifferentAddress = secondPerson.copy(
                address = secondPerson.address.copy(
                        postCode = "1111"
                )
        )

        assertThat(me.name, equalTo("Anders Sveen"))
        assertThat(secondPerson.name, equalTo("Someone Sveen"))
        assertThat(secondPersonDifferentAddress.address.postCode, equalTo("1111"))

        // Different objects, equals through data class
        assertThat(me.address, equalTo(secondPerson.address))
        // Equality, same as above
        assertTrue(me.address == secondPerson.address)
        // Object instance equality
        assertFalse(me.address === secondPerson.address)
        // Equality
        assertFalse(me.address == secondPersonDifferentAddress.address)
    }

    @Test
    fun testShowObjectMotherWithDataClass() {
        fun Person.Companion.valid(): Person {
            return Person(
                    "Anders Sveen",
                    Address("Mystreet", "64", "0547", "Norway"),
                    "99999999"
            )
        }

        fun Person.emptyAddress(): Person {
            return this.copy(address = Address("", "", "", ""))
        }

        val testPerson = Person.valid().let {
            it.copy(address = it.address.copy(country = "Sweden"))
        }

        assertThat(testPerson.livesInNorway, equalTo(false))
        assertThat(testPerson.emptyAddress().address.streetName, equalTo(""))
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
    fun testInfixFunctions() {
        infix fun Int.plus(addition: Int): Int {
            return this + addition
        }

        assertThat(1 plus 2, equalTo(3))
    }

    @Test
    fun testTransforms() {
        data class FamilyStatus(val name: String, val children: Int)

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

        val myMapOfNamesAndJustNumbers: Map<String, Int> = myList
                .map { it.name to it.children }
                .toMap()
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
    fun testTransaction() {
        val result = inTransaction { transactionId ->
            // Insert something you need with transactionId
            1000L
        }

        assertThat(result, equalTo(1000L))
    }

    fun <T> inTransaction(transactionOperation: (transactionId: UUID) -> T): T {
        // Set up transaction and do what you need
        val transactionId = UUID.randomUUID()
        return try {
            // Execute function and pass relevant information and objects
            transactionOperation(transactionId)
        } catch (e: Exception) {
            // Clean up and close
            throw java.lang.IllegalStateException("Something went wrong in your transaction")
        }
    }

}

