package coroutines.sharedMutableStateAndConcurrency

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * @author gihyeon-kim
 */

suspend fun main() {
    problem()
    solutionFineGrained()
    solutionCoarseGrained()
}

suspend fun problem() {
    var counter = 0

    withContext(Dispatchers.Default) {
        runWith100Coroutine10000times {
            counter++
        }
    }

    println("Counter = $counter")
}

suspend fun solutionFineGrained() {
    val beginTime = System.currentTimeMillis()
    val counterContext = newSingleThreadContext("CounterContext")
    var counter = 0

    withContext(Dispatchers.Default) {
        runWith100Coroutine10000times {
            // confine each increment to a single-threaded context
            withContext(counterContext) {
                counter++
            }
        }
    }
    println("Counter = $counter")
    println("Time: ${System.currentTimeMillis() - beginTime}")
}


suspend fun solutionCoarseGrained() {
    val beginTime = System.currentTimeMillis()
    val counterContext = newSingleThreadContext("CounterContext")
    var counter = 0

    withContext(counterContext) {
        runWith100Coroutine10000times {
            counter++
        }
    }
    println("Counter = $counter")
    println("Time: ${System.currentTimeMillis() - beginTime}")
}

/**
 * 100개의 코루틴이 각자 1000번씩 action을 수행
 */
suspend fun runWith100Coroutine10000times(action: suspend () -> Unit) {
    val n = 100  // number of coroutines to launch
    val k = 1000 // times an action is repeated by each coroutine
    val time = measureTimeMillis {
        coroutineScope { // scope for coroutines
            repeat(n) {
                launch {
                    repeat(k) { action() }
                }
            }
        }
    }
    println("Completed ${n * k} actions in $time ms")
}
