package coroutines

import kotlinx.coroutines.*

/**
 * @author gihyeon-kim
 */

val threadLocal = ThreadLocal<String?>() // declare thread-local variable

fun main() = runBlocking {
    threadLocal.set("main")
    println("Pre-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
    val job = launch(Dispatchers.Default + threadLocal.asContextElement(value = "launch")) {
        println("Launch start, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
        yield()
        println("After yield 1, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")

        // 스레드 로컬 값을 변경
        threadLocal.set("after yield")
        yield()
        println("After yield 2, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")

        // withContext를 사용하여 스레드 로컬 값을 변경
        withContext(threadLocal.asContextElement("withContext")) {
            println("After withContext, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
        }

    }
    job.join()
    println("Post-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
}
