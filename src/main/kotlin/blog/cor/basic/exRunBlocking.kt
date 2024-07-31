package blog.cor.basic

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

/**
 * @author gihyeon-kim
 */
fun main() = runBlocking {
//    launch {
//        delay(1000L)
//        println("World!")
//    }
//
//    println("Hello,")
//
//    coroutineScope {
//        launch {
//            delay(1000L)
//            println("World!")
//        }
//
//    }
//    println("Hello,")

//    coroutineScope {
//        launch {
//            delay(1000L)
//            println("World!")
//        }
//        println("Hello,")
//    }
    makeCoroutine()
    notMakeCoroutine()
}

fun makeCoroutine() = runBlocking {
    println("made coroutine")
    delay(1000L)
}

suspend fun notMakeCoroutine() = coroutineScope {
    println("not made coroutine")
    delay(1000L)
}

