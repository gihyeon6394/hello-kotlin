package coroutines

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

/**
 * @author gihyeon-kim
 */
fun simple4(): Flow<Int> = flow {
    for (i in 1..3) {
        println("Emitting $i")
        emit(i) // emit next value
    }
}

fun simple5(): Flow<String> =
    flow {
        for (i in 1..3) {
            println("Emitting $i")
            emit(i) // emit next value
        }
    }.map { value ->
        check(value <= 1) { "Crashed on $value" }
        "string $value"
    }

fun simple6(): Flow<Int> = flow {
    for (i in 1..3) {
        println("Emitting $i")
        emit(i)
    }
}

fun main() = runBlocking {
    try {
        simple4().collect { value ->
            println(value)
            check(value <= 1) { "Collected $value" }
        }
    } catch (e: Throwable) {
        println("Caught $e")
    }

    try {
        simple5().collect { value ->
            println(value)
        }
    } catch (e: Throwable) {
        println("Caught $e")
    }


//    simple6()
//        .catch { e -> println("Caught $e") } // does not catch downstream exceptions
//        .collect { value ->
//            check(value <= 1) { "Collected $value" }
//            println(value)
//        }

    simple6()
        .onEach { value ->
            check(value <= 1) { "Collected $value" }
            println(value)
        }
        .catch { e -> println("Caught $e") }
        .collect()
}
