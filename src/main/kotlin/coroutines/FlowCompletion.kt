package coroutines

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.runBlocking

/**
 * @author gihyeon-kim
 */
fun simple7(): Flow<Int> = (1..3).asFlow()

fun main() = runBlocking {

    simple7()
        .onCompletion { cause -> println("Flow completed with $cause") }
        .collect { value ->
            check(value <= 3) { "Collected $value" }
            println(value)
        }

    simple7()
        .onCompletion { cause -> println("Flow completed with $cause") }
        .collect { value ->
            check(value <= 1) { "Collected $value" }
            println(value)
        }
}
