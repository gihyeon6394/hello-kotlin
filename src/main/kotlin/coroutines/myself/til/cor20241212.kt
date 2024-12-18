package coroutines.myself.til

import kotlinx.coroutines.*

/**
 * @author gihyeon-kim
 */
val singleThreadContext = newSingleThreadContext("singleThreadContext")

/**
 * suspend 함수 안에서 스레드를 블로킹하면 발생하는 일
 *
 * 결론 : suspend (코루틴 중지 가능 함수) 함수여도 스레드를 블로킹하면 throughput (처리량) 떨어진다.
 *
 * job2에서 스레드를 블로킹하는 바람에 job1을 실행할 스레드가 없다.
 */
fun main() = runBlocking {
    withContext(singleThreadContext) {
        val job1 = async {
            some_logic()
        }
        delay(200)
        val job2 = async {
            suspended_but_block_thread()
        }
    }
}

suspend fun suspended_but_block_thread() = coroutineScope {
    println("block thread ..")
    Thread.sleep(1000) // Replace this "Thread.sleep()" call with "delay()".
//    delay(1000)
    println("release thread ..")
}

suspend fun some_logic() = coroutineScope {
    println("begin some logic ..")
    repeat(10) {
        delay(50)
        println("[$it] some logic .. ")
    }
    println("end some logic ..")
}
