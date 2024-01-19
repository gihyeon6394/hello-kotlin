fun hello() {
    println("Hello, world!")
}

fun main() {
    hello()
    println(sum(1, 2))

    printMsgWithPrefix("Hello", "Info")
    printMsgWithPrefix("Hello")
    printMsgWithPrefix(prefix = "Log", msg = "Hello")

    printMsg("Hello")

    println(circleArea(2))
    println(circleAreaSingle(2))

    // lamda expression
    println({ str: String -> str.uppercase() }("hello"))

    val upperCaseStr = { str: String -> str.uppercase() }
    println(upperCaseStr("hello"))

    val numbers = listOf(1, 2, 3, 4, 5)

    val positives = numbers.filter { x -> x > 0 }
    println(positives)

    val doubled = numbers.map { x -> x * 2 }
    println(doubled)

    val timesInMin = listOf(2, 10, 15, 1)
    val min2sec = toSecs("min")
    val totalSecs = timesInMin.map(min2sec).sum()
    println(totalSecs)

    listOf(1, 2, 3).fold(0) { x, item -> x + item }

    // Exercise 1
    val actions = listOf("title", "year", "author")
    val prefix = "https://example.com/book-info"
    val id = 5
    val urls = actions.map { el -> "$prefix/$id/$el" }
    println(urls)

    repeatN(3) { println("Hello") }

}


fun sum(x: Int, y: Int): Int {
    return x + y
}

fun printMsgWithPrefix(msg: String, prefix: String = "Info") {
    println("[$prefix] $msg")
}

fun printMsg(msg: String) {
    println(msg)
    // return Unit or return is optional
}


// Exercise 1
fun circleArea(radius: Int): Double {
    return Math.PI * radius * radius
}

fun circleAreaSingle(radius: Int) = Math.PI * radius * radius

fun intervalInSeconds(hours: Int = 0, min: Int = 0, sec: Int = 0) = ((hours * 60) + min) * 60 + sec

fun toSecs(time: String): (Int) -> Int = when (time) {
    "hour" -> { value -> value * 60 * 60 }
    "min" -> { value -> value * 60 }
    "sec" -> { value -> value }
    else -> { value -> value }
}

// Exercise 2
fun repeatN(n: Int, action: () -> Unit) {
    for (i in 1..n) {
        action()
    }
}
