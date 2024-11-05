package coroutines.coroutineExceptionHandling.cancellable

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * @author gihyeon-kim
 */
fun main() = runBlocking {

    // 부모의 coroutineScope을 사용한다.
    launch {
        delay(1200L)
    }

    // Dispatchers.Default 를 사용한다.
    launch(Dispatchers.Default) {
        delay(100L)
    }

    println("========================================")
    delay(1300L)
    println("end")
}


