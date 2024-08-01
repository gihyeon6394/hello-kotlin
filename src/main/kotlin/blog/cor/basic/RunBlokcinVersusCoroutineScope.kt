package blog.cor.basic

import kotlinx.coroutines.*
import java.util.concurrent.Executors

/**
 * @author gihyeon-kim
 */
fun main() = runBlocking {
    // 차이점 1. 코루틴 생성 여부
    makeCoroutine()
    notMakeCoroutine()

    // 차이점 2. thread release 여부 (blocking)
    demoWithCoroutineScope()
    demoWithRunBlocking()
}

/**
 * runBlocking은 새로운 코루틴을 생성해 실행한다.
 * 스레드 세상과 코루틴 세상의 연결다리 (bridge) 역할을 한다.
 */
fun makeCoroutine() = runBlocking {
    println("made coroutine")
    delay(1000L)
}

/**
 * coroutineScope는 새로운 코루틴을 생성하지 않는다.
 * 해당 스코프로 지정된 suspend block을 실행한다.
 * 해당 스코프는 suspend block이다.
 */
suspend fun notMakeCoroutine() = coroutineScope {
    println("not made coroutine")
    delay(1000L)
}

private val context = Executors.newFixedThreadPool(2).asCoroutineDispatcher()

fun demoWithCoroutineScope() = runBlocking {
    (1..10).forEach {
        launch(context) {
            coroutineScope {
                println("Start No.$it in coroutineScope on ${Thread.currentThread().name}")
                delay(500) // 코루틴 suspend
                println("End No.$it in coroutineScope on ${Thread.currentThread().name}")
            }
        }
    }
}

fun demoWithRunBlocking() = runBlocking {
    (1..10).forEach {
        launch(context) {
            runBlocking { // 현재 스레드를 blocking (코루틴을 현재 스레드에 반인딩)
                println("Start No.$it in runBlocking on ${Thread.currentThread().name}")
                delay(500) // 코루틴 suspend
                println("End No.$it in runBlocking on ${Thread.currentThread().name}")
            }
        }
    }
}
