package coroutines.sharedMutableStateAndConcurrency

import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * @author gihyeon-kim
 */
val counterContext2 = newSingleThreadContext("CounterContext")
var counter2 = 0

fun main() = runBlocking {
    // confine everything to a single-threaded context
    withContext(counterContext2) {
        massiveRun {
            counter2++
        }
    }
    println("Counter = $counter2")
}
