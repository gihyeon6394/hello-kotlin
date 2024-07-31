package coroutines.sharedMutableStateAndConcurrency

import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

/**
 * @author gihyeon-kim
 * @see https://www.baeldung.com/kotlin/inappropriate-blocking-call
 */

fun main() = runBlocking {
//    log("runBlocking start", "$coroutineContext")
//    (1..3).forEach {
//        launch {
//            sleepThread(it)
//        }
//    }
//    log("runBlocking end", "$coroutineContext")

//    coroutineScope {
        launch {
            delay(1000L)
            log("World!", "$coroutineContext")
        }
//    }
    log("Hello", "$coroutineContext")
}

suspend fun sleepThread(num: Int) {
    withContext(Dispatchers.IO) {
        log("start $num", "$coroutineContext")
        Thread.sleep(500L) // 새로운 코루틴 스코프에서 스레드를 재우지 않으면, Possibly blocking call in non-blocking context could lead to thread starvation
        log("end $num", "$coroutineContext")
    }
}

private fun log(msg: String, coroutineCtx: String) = println("[$coroutineCtx - ${Thread.currentThread().name}] $msg")
