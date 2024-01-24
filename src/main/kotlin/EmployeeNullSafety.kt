data class EmployeeNullSafety(val name: String, var salary: Int) {
}

fun employeeById(id: Int) = when (id) {
    1 -> EmployeeNullSafety("Mary", 20)
    2 -> null
    3 -> EmployeeNullSafety("John", 21)
    else -> null
}

fun salaryById(id: Int) = employeeById(id)?.salary ?: 0

fun main() {
    println((1..5).sumOf { id -> salaryById(id) })
}

