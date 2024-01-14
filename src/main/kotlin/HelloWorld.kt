fun main() {
    println("Hello Kotlin!")

    // Variables
    // val : read-only variable
    val popcorn = 5
    val hotdog = 7
    val customers = 10

    // customers = 20 // Error: Val cannot be reassigned

    // var : mutable variable
    var total = 0
    total = (popcorn * 3) + (hotdog * 2)
    println("Total: $total")

    // String templates
    println("Total: $total")
    println("there are ${customers * 100} customers")

    // Practice

    val name = "Mary"
    val age = 20
    println("$name is $age years old")
}