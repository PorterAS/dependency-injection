import java.time.LocalDate
import java.util.*

data class Deviation(val description: String)

data class Order(
        val id: UUID? = null,
        val date: LocalDate,
        val comment: String,
        val deviations: List<Deviation> = emptyList()
) {
    companion object

    fun canBeApproved(): Boolean {
        return deviations.isEmpty()
    }

}
