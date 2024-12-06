package coroutines.myself.blog.vsVirtualThread

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.lang.Thread.sleep

/**
 * @author gihyeon-kim
 *
 * https://tech.kakaopay.com/post/coroutine_virtual_thread_wayne/?utm_source=oneoneone
 */
fun main() {
    coroutineParallelTest()
}

fun coroutineParallelTest(): Unit = runBlocking {
    val results = mutableListOf<Int>()
    val jobs = List(100) {
        launch {
            // 컨텍스트를 IO로 변경하면서 코루틴을 병렬로 실행
            withContext(Dispatchers.IO) {
                sleep(100L)
                synchronized(results) {
                    results.add(it)
                }
                println("Adding $it")
            }
        }
    }
    jobs.forEach { it.join() }
}
