package coroutines.coroutineExceptionHandling

import kotlinx.coroutines.*

/**
 * @author gihyeon-kim
 */

fun main() = runBlocking {
    val job1 = launch {
        val child1 = launch {
            try {
                delay(Long.MAX_VALUE)
            } finally {
                println("Child is cancelled")
            }
        }
        yield()
        println("Cancelling child")
        child1.cancel()
        child1.join()
        yield()
        println("Parent is not cancelled")
    }
    job1.join()

    println("========================================")


    val handler = CoroutineExceptionHandler { _, exception ->
        println("CoroutineExceptionHandler got $exception")
    }
    val job2 = GlobalScope.launch(handler) {
        launch { // the first child
            try {
                delay(Long.MAX_VALUE)
            } finally {
                withContext(NonCancellable) {
                    println("Children are cancelled, but exception is not handled until all children terminate")
                    delay(100)
                    println("The first child finished its non cancellable block")
                }
            }
        }
        launch { // the second child
            delay(10)
            println("Second child throws an exception")
            throw ArithmeticException()
        }
    }
    job2.join()
}
