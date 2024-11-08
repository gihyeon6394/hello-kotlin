package coroutines.myself.basics

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking

/**
 * @author gihyeon-kim
 */

val dispatcherSingleThread = newSingleThreadContext("myThread")

fun main() = runBlocking(dispatcherSingleThread) {
//    fuRunBlocking()
    fuCoroutineScope()
    println("Hello,")
}

fun fuRunBlocking() = runBlocking {
    delay(1000L)
    println("World!")
}

suspend fun fuCoroutineScope() = coroutineScope {
    delay(1000L)
    println("World!")
}
