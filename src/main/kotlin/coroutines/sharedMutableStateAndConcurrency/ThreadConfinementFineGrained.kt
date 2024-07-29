package coroutines.sharedMutableStateAndConcurrency

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * @author gihyeon-kim
 */
val counterContext = newSingleThreadContext("CounterContext")
var counter1 = 0

fun main() = runBlocking {
    withContext(Dispatchers.Default) {
        massiveRun {
            // confine each increment to a single-threaded context
            withContext(counterContext) {
                counter1++
            }
        }
    }
    println("Counter = $counter1")
}
