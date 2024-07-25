package coroutines.sharedMutableStateAndConcurrency

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author gihyeon-kim
 */
val counterAtomic = AtomicInteger()

fun main() = runBlocking {
    withContext(Dispatchers.Default) {
        massiveRun {
            counterAtomic.incrementAndGet()
        }
    }
    println("Counter = $counterAtomic")
}
