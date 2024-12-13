package coroutines.myself.til

import kotlinx.coroutines.*

/**
 * @author gihyeon-kim
 */
val singleThreadContext = newSingleThreadContext("singleThreadContext")

/**
 *
 * job2에서 스레드를 블로킹하는 바람에 job1을 실행할 스레드가 없다.
 * 즉 suspend 함수에서 스레드를 블로킹하면 안된다.
 */
fun main() = runBlocking {
    withContext(singleThreadContext) {
        val job1 = async {
            someLogic()
        }
        delay(1000)
        val job2 = async {
            suspended_but_block_thread()
        }
    }
}

suspend fun suspended_but_block_thread() = coroutineScope {
    println("block thread ..")
//    Thread.sleep(1000)
    delay(1000)
    println("release thread ..")
}

suspend fun someLogic() = coroutineScope {
    println("begin someLogic ..")
    repeat(10) {
        delay(500)
        println("[$it] some logic .. ")
    }
    println("end someLogic ..")
}
