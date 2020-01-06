import java.time.LocalDate

data class Deviation(val description: String)

data class Order(val id: String, val date: LocalDate, val deviations: List<Deviation>) {
    companion object

    fun canBeApproved(): Boolean {
        return deviations.isEmpty()
    }

}
