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

suspend fun fuRunBlocking() = runBlocking {
    delay(1000L)
    println("World!")
}

suspend fun fuCoroutineScope() = coroutineScope {
    delay(1000L)
    println("World!")
}
