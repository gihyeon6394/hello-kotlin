package coroutines.myself.til

import coroutines.log
import kotlinx.coroutines.*

/**
 *
 * CoroutineExceptionHandler 은 오직 자식 코루틴들에게서 발생한 uncaught exception을 처리하는 루트 코루틴의 컨텍스트에 추가되는 핸들러이다.
 * uncaught exception :  try-catch로 처리되지 않은 예외
 * children coroutine : 어떤 코루틴의 coroutine Context을 상속 받아서 생긴 코루틴
 * children coroutine은 자신의 uncaught excpeiont 예외처리를 부모 코루틴에게 위임 -> 부모는 다시 그 상위 부모까지 위임해 최상위 (Root) 코루틴까지 위임됨
 * 따라서 자식 코루틴의 컨텍스트에 handler를 추가해도 그 handler가 동작하지 않음
 * aysnc빌더는 항상 모든 예외를 잡아 Deferred 객체에 넣어두기때문에 Handler가 동작하지 않음
 *
 * @author gihyeon-kim
 */
fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")
const val COROUTINE_NAME_MAIN = "coroutine-main"
const val COROUTINE_NAME_NEW = "coroutine-new"

fun main() {

//    ex01()
//    ex02()
//    ex03()
//    ex04()
//    ex05()
//    ex06()
    ex07()
    log("program end")
}

val handler = CoroutineExceptionHandler { _, exception ->
    log("there is an error in program $exception")
}

suspend fun boom() {
    delay(100)
    log("boom")
    throw Exception("Boom")
}

/**
 * launch 빌더 안에서 uncaught exception이 발생하면 루트 코루틴까지 전파된다.
 * 결과 : 에러 파싱 못함
 */
fun ex01() = runBlocking(CoroutineName(COROUTINE_NAME_MAIN)) {
    log("start method")
    launch(CoroutineName(COROUTINE_NAME_NEW)) {
        boom()
    }
    delay(2000)
    log("done")
}


/**
 * handler를 루트 코루틴 컨텍스트 (runblocking)에 추가
 *
 * 결과 : 에러 파싱 못함
 *
 * 1. 자식 코루틴에서 uncaught exception이 발생 -> 루트 코루틴까지 전파
 * 2. 루트 코루틴이 예외를 받고 코루틴이 취소되며 프로그램 종료
 *
 * 특이 : runBlocking 루트 코루틴에 핸들러는 무소용. runblcoking의 메인 코루틴은 모든 자식 코루틴이 취소되면 핸들러가 있더라도 그냥 취소된다
 */
fun ex02() = runBlocking(CoroutineName(COROUTINE_NAME_MAIN) + handler) {
    log("start method")
    launch(CoroutineName(COROUTINE_NAME_NEW)) {
        boom()
    }
    delay(2000)
    log("done")
}


/**
 * 자식 코루틴의 컨텍스트에 handler를 추가
 *
 * 결과 : 에러 파싱 못함
 *
 * 1. 자식 코루틴에서 uncaught exception이 발생 -> 루트 코루틴까지 전파 (자식 코루틴의 handler는 무시됨)
 *
 * > delegate handling of their exceptions to their parent coroutine, which also delegates to the parent, and so on until the root, so the CoroutineExceptionHandler installed in their context is never used.
 * @see kotlinx.coroutines.CoroutineExceptionHandler
 */
fun ex03() = runBlocking(CoroutineName(COROUTINE_NAME_MAIN)) {
    log("start method")

    launch(CoroutineName(COROUTINE_NAME_NEW) + handler) {
        boom()
    }
    delay(2000)
    log("done")
}

/**
 * 또 다른 스코프 + 새로운 컨텍스트의 루트 코루틴에 handler 추가
 * 결과  : 에러 파싱함
 *
 * 1. main coroutine 생성
 * 2. 새로운 코루틴 컨텍스트 + 해당 컨텍스트의 루트 코루틴에 handler 추가
 * 3. 새로운 루트 코루틴에서 uncaught exception이 발생 -> 현재 코루틴이 취소되고, launch 빌더의 핸들러가 동작
 */
fun ex04() = runBlocking(CoroutineName(COROUTINE_NAME_MAIN)) {
    log("start method")
    CoroutineScope(Dispatchers.IO + handler)
        .launch {
//        .launch(handler) { // 이래도 동작은 같음
            boom()
        }
    delay(2000)
    log("done")
}

/**
 * 또 다른 스코프의 코루틴에서 uncaught exception이 발생
 * 결과 : 에러 파싱 못함
 *
 * 1. 또다른 스코프에서 생성되었지만 루트 코루틴이 아닌 자식 코루틴임
 */
fun ex05() = runBlocking(CoroutineName(COROUTINE_NAME_MAIN)) {
    log("start method")
    coroutineScope {
        launch(handler) {
            boom()
        }
    }
    delay(2000)
    log("done")
}


/**
 * supervisorScope 를 사용해 해당 컨텍스트의 자식 코루틴들은 자신의 uncaught exception을 부모에게 위임하지 않고 자신이 처리. 즉 자신에게 handler가 있으면 그 handler가 동작
 * 결과 : 에러 파싱됨
 */
fun ex06() = runBlocking(CoroutineName(COROUTINE_NAME_MAIN)) {
    log("start method")
    supervisorScope {
        launch(CoroutineName(COROUTINE_NAME_NEW) + handler) {
            boom()
        }
        delay(2000)
        log("done")
    }
}


/**
 * withContext를 사용해 새로운 context로 전환 (context 전화해도 부모-자식 관계는 유지됨)
 * 결과 : 에러 파싱 못함
 *
 * $COROUTINE_NAME_NEW-2 은 $COROUTINE_NAME_NEW-1의 자식 코루틴 (자식 Job)
 * $COROUTINE_NAME_NEW-1은 $COROUTINE_NAME_MAIN의 자식 코루틴 (자식 Job)
 */
fun ex07() = runBlocking(CoroutineName(COROUTINE_NAME_MAIN)) {
    log("start method")
    launch(CoroutineName("$COROUTINE_NAME_NEW-1")) {
        withContext(Dispatchers.IO + handler) {
            launch(CoroutineName("$COROUTINE_NAME_NEW-2")) {
                delay(1000)
            }
            delay(2000)
            log("done whatever~")
        }
        log("out of withContext")
    }
    log("out of launch")
    delay(1500)
    boom()
}

