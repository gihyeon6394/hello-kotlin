package coroutines.myself.basics

import kotlinx.coroutines.*

/**
 * @author gihyeon-kim
 */

val dispatcherSingleThread = newSingleThreadContext("myThread")

fun main() = runBlocking(dispatcherSingleThread) {
    launch {
//        fuRunBlocking()
        fuCoroutineScope()
    }
    delay(300L)
    println("Hello,")
}

// delay 로 코루틴을 중지시켜도 runBlocking 블럭 안에서 실행되기 때문에 현재 스레드를 블로킹한다
fun fuRunBlocking() = runBlocking {
    delay(1000L)
    println("World!")
}

suspend fun fuCoroutineScope() = coroutineScope {
    delay(1000L)
    println("World!")
}
