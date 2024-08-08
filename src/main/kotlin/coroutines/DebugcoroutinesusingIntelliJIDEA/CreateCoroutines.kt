package coroutines.DebugcoroutinesusingIntelliJIDEA

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

/**
 * @author gihyeon-kim
 */
fun main() = runBlocking { // coroutine으로 wrapping
    println("Hello, World!")

    val a = async { // deffered value 6을 반환하는 코루틴 생성
        println("I'm computing part of the answer")
        6
    }

    val b = async {// deffered value 7을 반환하는 코루틴 생성
        println("I'm computing the other part of the answer")
        7
    }
    println("The answer is ${a.await() * b.await()}")
}
