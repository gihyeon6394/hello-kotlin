package blog.cor.coarseVsFine

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * @author gihyeon-kim
 */

/**
 * solutionFineGrained 보다 solutionCoarseGrained가 더 빠르다.
 *
 * - solutionFineGrained 은 100번의 스레드 문맥교환이 발생한다 (멀티 스레드 -> 싱글 스레드)
 * - solutionCoarseGrained 는 1번의 스레드 문맥교환이 발생한다 (멀티 스레드 -> 싱글 스레드)
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
            // 스레드 문맥 교환
            withContext(counterContext) {
                counter++ // 크리티컬 섹션이자 오래걸리는 비즈니스 로직
            }
        }
    }
}

suspend fun solutionCoarseGrained() {
    val counterContext = newSingleThreadContext("CounterContext")
    var counter = 0

    withContext(counterContext) {
        massiveRun {
            counter++ // 크리티컬 섹션이자 오래걸리는 비즈니스 로직
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
