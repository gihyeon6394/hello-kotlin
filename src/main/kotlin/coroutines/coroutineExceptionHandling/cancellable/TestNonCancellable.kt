import kotlinx.coroutines.*

fun main() = runBlocking {

    testBasic()
//    test2()
//    test3()
}

fun testBasic() = runBlocking {
    val job = launch(Dispatchers.Default) {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            Thread.sleep(500L)
        }
    }

    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // cancels the job and waits for its completion
    println("main: Now I can quit.")
}


// 코루틴을 실행하던 중 해당 코루틴이 취소되어도,
// NonCancellable을 사용하면 해당 코루틴은 반드시 해당 블럭을 실행하게 된다.
fun test2() = runBlocking {
    val job = launch (Dispatchers.Default){
        try {
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                delay(500L)
            }
        } finally {
            println("I'm running finally")
        }
    }

    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // cancels the job and waits for its completion
    println("main: Now I can quit.")
}

fun test3() = runBlocking {
    val job2 = launch {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }

//        withContext(NonCancellable) {
//            println("this is non-cancellable block start")
//            delay(1000L)
//            println("this is non-cancellable block end")
//        }
    }
    job2.invokeOnCompletion { exception: Throwable? ->
        println("job2 completed with exception: $exception")
        println("this is non-cancellable block start")
        println("this is non-cancellable block end")
    }

    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job2.cancelAndJoin() // cancels the job and waits for its completion
    println("main: Now I can quit.")
}
