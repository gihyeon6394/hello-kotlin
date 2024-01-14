/**
 * Basic Types
 * Category    Basic Types
 * Integers    Byte, Short, Int, Long
 * Unsigned integers    UByte, UShort, UInt, ULong
 * Floating-point numbers    Float, Double
 * Booleans    Boolean
 * Characters    Char
 * Strings    String
 *
 */

fun main() {
    // kotlin type inference
    var customers = 10 // Int

    customers = 20;
    println("customers: $customers")
    customers = customers - 5
    println("customers: $customers")

    customers -= 5
    println("customers: $customers")

    customers *= 2
    println("customers: $customers")


    // declare without initialization
    var dd: Int

    // initialize later
    dd = 10

    // declare and initialize explicitly type
    val ee: Int = 10

    println("d: $dd")
    println("e: $ee")

    // exercise
    val a = 1000 // Int
    val b = "log message" // String
    val c = 3.14159 // Double
    val d = 100_000_000_000_000 // Long
    val e = false // Boolean
    val f = '\n' // Char
}
