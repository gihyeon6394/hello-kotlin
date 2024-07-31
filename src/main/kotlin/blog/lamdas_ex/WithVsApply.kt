package blog.lamdas_ex

/**
 * @author gihyeon-kim
 * @see https://kotlinlang.org/docs/scope-functions.html#run
 */
fun main() {
    println(alphabetWith())
    println(alphabetApply())
}

fun alphabetWith(): String = with(StringBuilder()) {
    for (letter in 'A'..'Z') {
        append(letter)
    }
    append("\nNow I know the alphabet!")
    toString()
}

fun alphabetApply(): String = StringBuilder().apply {
    for (letter in 'A'..'Z') {
        append(letter)
    }
    append("\nNow I know the alphabet!")
}.toString()
