import kotlinx.coroutines.*

fun main() = runBlocking {

//    testBasic()
//    test2()
//    test3()
    testCancelButNotCancelled()
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
// finally 블럭은 반드시 실행된다.
fun test2() = runBlocking {
    val job = launch {
        try {
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                delay(500L)
            }
        } finally {
            println("I'm running finally")
            withContext(NonCancellable) {
                println("this is non-cancellable block start")
                delay(1000L)
                println("this is non-cancellable block end")
            }
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


/**
 * 코루틴을 취소시켜도 취소되지 않고 계속되는 경우
 * 쿠루틴의 취소여부는 내부에서 suspend 함수를 호출할때마다 체크하기 때문
 * -> suspend 함수가 호출되지 않으면 취소되지 않는다.
 */
fun testCancelButNotCancelled() = runBlocking {
    val job = launch {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
        }
    }
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // 취소 후 기다림
    println("main: Now I can quit.")
}
