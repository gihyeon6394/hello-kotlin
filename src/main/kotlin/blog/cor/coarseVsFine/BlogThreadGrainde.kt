package blog.cor.coarseVsFine

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * @author gihyeon-kim
 */

fun main() = runBlocking {
    solutionFineGrained()
    solutionCoarseGrained()
}

suspend fun solutionFineGrained() {
    val counterContext = newSingleThreadContext("CounterContext")
    var counter = 0

    withContext(Dispatchers.Default) {
        massiveRun {
            withContext(counterContext) {
                counter++
            }
        }
    }
}

suspend fun solutionCoarseGrained() {
    val counterContext = newSingleThreadContext("CounterContext")
    var counter = 0

    withContext(counterContext) {
        massiveRun {
            counter++
        }
    }
}

/**
 * 100개의 코루틴이 각자 1000번씩 action을 수행
 */
suspend fun massiveRun(action: suspend () -> Unit) {
    val n = 100  // number of coroutines to launch
    val k = 1000 // times an action is repeated by each coroutine
    val time = measureTimeMillis {
        coroutineScope { // scope for coroutines
            repeat(n) {
                launch {
                    repeat(k) {
                        action()
                    }
                }
            }
        }
    }
    println("Completed ${n * k} actions in $time ms")
}
