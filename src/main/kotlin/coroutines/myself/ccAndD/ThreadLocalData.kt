package coroutines.myself.ccAndD

import kotlinx.coroutines.*

/**
 * @author gihyeon-kim
 */
val threadLocal = ThreadLocal<String?>() // declare thread-local variable


/**
 * 스레드 로컬 데이터를 코루틴간에 전달할 수 있지만, 데이터가 변했다고해서, 전파가 되진 않는다.
 */
fun main() = runBlocking {
    threadLocal.set("main")
    println("Pre-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
    val job = launch(Dispatchers.Default + threadLocal.asContextElement(value = "launch")) {
        println("Launch start, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
        yield()
        println("After yield, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")

        launch(threadLocal.asContextElement(value = "nested launch")) {
            println("Nested launch, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
            yield()
            println("After yield, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
        }.join()

        println("Launch end, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
    }
    job.join()
    println("Post-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
}
