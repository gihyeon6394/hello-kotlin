data class Employee(val name: String, var salary: Int) {

    class RandomEmployeeGenerator(var minSalary: Int, var maxSalary: Int) {
        val randomNameList = listOf("Alex", "Max", "Mary", "John", "Mark", "Alice", "Bob", "Jane", "Tom", "Jack")

        fun generateEmployee(): Employee {
            var randSalary = (minSalary..maxSalary).random()
            var randName = randomNameList.random()
            return Employee(randName, randSalary)
        }
    }
}

fun main() {
    val emp = Employee("Mary", 20)
    println(emp)
    emp.salary += 10
    println(emp)

    val empGen = Employee.RandomEmployeeGenerator(10, 30)
    println(empGen.generateEmployee())
    println(empGen.generateEmployee())
    println(empGen.generateEmployee())
    empGen.minSalary = 50
    empGen.maxSalary = 100
    println(empGen.generateEmployee())
}
