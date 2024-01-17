fun main() {
    val d: Int
    val chk = true;

    if (chk) {
        d = 10
    } else {
        d = 20
    }

    println("d: $d")

    // if 를 expression 으로 사용
    val e = if (chk) 10 else 20
    println("e: $e")

    // when expression or statement

    // when statement
    when (e) {
        10 -> println("e is 10")
        20 -> println("e is 20")
        else -> println("e is neither 10 nor 20")
    }

    // when expression
    val result = when (e) {
        10 -> "e is 10"
        20 -> "e is 20"
        else -> "e is neither 10 nor 20" // else 는 필수
    }

    println("result: $result")

    // Ranges
    // .. operator로 range를 만들 수 있다.
    // e.g. 1..4 -> 1, 2, 3, 4

    // Loops

    // For
    for (number in 1..5) {
        println("number: $number")
    }

    val aespa = listOf("Winter", "Karina", "Giselle", "Ningning")
    for (member in aespa) {
        println("member: $member")
    }

    // while

    var cakesEaten = 0
    while (cakesEaten < 5) {
        println("Eat more cake!")
        cakesEaten++
    }

    var bakeCake = 0
    do {
        println("Bake a cake")
        bakeCake++
    } while (bakeCake < 5)

    // Exercise 1
    val button = "A"

    println(
            when (button) {
                "A" -> "Yes"
                "B" -> "No"
                "X" -> "Menu"
                "Y" -> "Nothing"
                else -> "There is no button"
            }
    )

    // Exercise 2
    var pizzaSlices = 0
    while (pizzaSlices < 8) {
        pizzaSlices++
        println("There's only $pizzaSlices slice/s of pizza :(")
    }
    println("There are $pizzaSlices slices of pizza. Hooray! We have a whole pizza! :D")
    pizzaSlices = 1
    do {
        println("There's only $pizzaSlices slice/s of pizza :(")
        pizzaSlices++
    } while (pizzaSlices < 9)
    println("There are $pizzaSlices slices of pizza. Hooray! We have a whole pizza! :D")

    // Exercise 3
    for (number in 1..100) {
        when {
            // number % 3 == 0 && number % 5 == 0 -> println("FizzBuzz")
            number % 15 == 0 -> println("FizzBuzz")
            number % 3 == 0 -> println("Fizz")
            number % 5 == 0 -> println("Buzz")
            else -> println(number)
        }
    }

    // Exercise 4
    val words = listOf("dinosaur", "limousine", "magazine", "language")
    for (word in words) {
        if (word.startsWith("l")) println(word)
    }
}
