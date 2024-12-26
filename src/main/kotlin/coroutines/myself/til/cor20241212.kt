package coroutines.myself.til

import kotlinx.coroutines.*

/**
 * @author gihyeon-kim
 */

/**
 * suspend 함수 안에서 스레드를 블로킹하면 발생하는 일
 *
 * 결론 : suspend (코루틴 중지 가능 함수) 함수여도 스레드를 블로킹하면 throughput (처리량) 떨어진다.
 *
 * job2에서 스레드를 블로킹하는 바람에 job1을 실행할 스레드가 없다.
 */

val singleThreadContext = newSingleThreadContext("singleThreadContext")

fun main() = runBlocking {
    withContext(singleThreadContext) {
        val job1 = async {
            logic_1()
        }
        delay(200)
        val job2 = async {
            logic_2_blocking_thread()
        }
    }
}

suspend fun logic_1() = coroutineScope {
    println("begin some logic ..")
    repeat(10) {
        delay(50)
        println("[$it] some logic .. ")
    }
    println("end some logic ..")
}


suspend fun logic_2_blocking_thread() = coroutineScope {
    println("block thread ..")
    Thread.sleep(1000) // Replace this "Thread.sleep()" call with "delay()".
//    delay(1000)
    println("release thread ..")
}
