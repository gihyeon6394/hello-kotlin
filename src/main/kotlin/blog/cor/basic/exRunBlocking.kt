package blog.cor.basic

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    coroutineScope {
        launch {
            delay(1000L)
            println("World!")
        }
        println("Hello,")
    }
}



