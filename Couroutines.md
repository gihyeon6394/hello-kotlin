# Coroutines (kotlinx.coroutines)

- Coroutines basics
- Coroutines and channels-tutorial
- Cancellation and timeouts
- Composing suspending functions
- Coroutine context and dispatchers
- Asynchronous Flow
- Channels
- Coroutine exception handling
- Shared mutuable state and concurrency
- Select expression (experimental)
- Debug coroutines using IntelliJ IDEA - tutorial
- Debug Kotlin Flow using IntelliJ IDEA - tutorial

reference : https://kotlinlang.org/docs/coroutines-guide.html

---

### Coroutines

비동기, non-blocking은 개발에서 매우 중요한 부분이다.  
코틀린은 couroutine을 통해 비동기 프로그래밍을 쉽게 할 수 있도록 지원한다.

## Coroutines guide

- `async`, `await` 는 kotlin에서 keyword나 라이브러리가 아니다.
- **suspending function**은 더 안전하고 더 낮은 error-prone을 가진다. (futures, promises 보다)
- `kotlinx.coroutines` : 코루틴을 사용하기 위한 라이브러리 (by JetBrains)
    - `launch`, `async` 와 같은 고수준으로 코루틴을 사용할 수 있는 함수를 제공한다.
- `kotlinx-coroutines-core` : 코루틴을 사용하기 위한 라이브러리 (by JetBrains)

## Coroutines basics

### Your first coroutine

- **coroutine** : 중지가능한 연산을 하는 인스턴스
- 스레드와 비슷한 면 : 실행중인 코드 block 가능
- 코루틴은 스레드와 다름 : 스레드 안에서 중지 가능하고 다른 스레드에서 계속할 수 있다.

```kotlin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    launch {
        delay(1000L)
        println("World!")
    }

    println("Hello,")
}
```

```shell
Hello,
World!
```

- `launch` : 코루틴 빌더
    - 새로운 코루틴을 시작하고 실행한다.
- `delay` : 일시 중지 함수 (suspend function)
    - 코루틴을 특정 시간동안 중지
- `runBlocking` : 실행 중인 스레드 (`fun main()`)가 blocked
    - non coroutines world (`fun main()`)과 연결
    - 없이 `launch`를 사용하면 에러 발생 `Unresolved reference: launch`

#### Structured concurrency

- Structured concurrency : 새로운 코루틴은 CoroutineScope에서만 launch 가능
- CoroutineScope : 코루틴의 lifecycle, scope을 관리하는 인터페이스
- 위에서는 `runBlocking` 으로 새로운 CoroutineScope를 만들었다.
- CoroutineScope은 자식 코루틴들이 완료되기 전까지 실행을 끝내지 않는다.

### Extract function refactoring

- `launch` 를 별도의 함수로 추출
- `suspend` modifier : suspending function을 선언
    - suspending function : 코루틴 안에서 사용 가능한 함수이지만, 코루틴 중지 가능
- **suspending function** : 코루틴을 중지 가능한 함수 (코루틴 안에서 사용)

```kotlin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    launch { doWorld() }

    println("Hello,")
}

suspend fun doWorld() {
    delay(1000L)
    println("World!")
}
```

### Scope builder

- `coroutineScope` : 코루틴을 실행하는 블록을 만들어주는 함수

|     | `runBlocking`                      | `coroutineScope`                       |
|-----|------------------------------------|----------------------------------------|
| 공통점 | 코루틴을 실행하는 블록, children이 완료될떄까지 기다림 | 코루틴을 실행하는 블록, children이 완료될때까지 기다림     |
| 차이점 | 실행 중인 스레드를 block                   | 중지하고, thread 를 release하고, 다시 시작할 수 있다. |

```kotlin
fun main() = runBlocking {
    doWorld()
}

suspend fun doWorld() = coroutineScope {  // this: CoroutineScope
    launch {
        delay(1000L)
        println("World!")
    }
    println("Hello")
}
```

### Scope builder and concurrency

- `coroutineScope` builder는 suspending function 내부 어디에서든 사용 가능

```kotlin
// Sequentially executes doWorld followed by "Done"
fun main() = runBlocking {
        doWorld()
        println("Done")
    }

// Concurrently executes both sections
suspend fun doWorld() = coroutineScope { // this: CoroutineScope
    launch {
        delay(2000L)
        println("World 2")
    }
    launch {
        delay(1000L)
        println("World 1")
    }
    println("Hello")
}
```

### An explicit job

- `launch` builder는 Job 오브젝트를 반환
- 명시적으로 Job을 관리할 수 있음

```kotlin
import kotlinx.coroutines.*

suspend fun explicitJob() = coroutineScope {
    val job = launch {
        delay(1000L)
        println("World!")
    }

    println("Hello,")
    job.join() // wait until child coroutine completes
    println("Done")
}
```

### Coroutines are light-weight

- 코루틴은 JVM 스레드보다 resource를 적게 사용

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    repeat(50_000) { // 50000 개의 개별적인 코루틴 생성, launch는 suspend function (=스레드에게 제어권을 양보)
        launch {
            delay(5000L)
            print(".")
        }
    }
}

// using java thread
fun main() {
    repeat(50_000) {
        thread {
            Thread.sleep(5000L)
            print(".")
        }
    }
}
```

## Coroutines and channels-tutorial

- network request를 suspend function으로 만들기
- 코루틴을 사용해서 request를 concurrently하게 실행
- 채널을 사용해 코루틴 간에 정보 전달

### Before you start

### Blocking requests

![img.png](img.png)

```kotlin
interface GitHubService {
    /**
     * Get a list of the repositories for the organization.
     */
    @GET("orgs/{org}/repos?per_page=100")
    fun getOrgReposCall(
        @Path("org") org: String
    ): Call<List<Repo>>

    /**
     * Get a list of the contributors to a repository.
     */
    @GET("repos/{owner}/{repo}/contributors?per_page=100")
    fun getRepoContributorsCall(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Call<List<User>>
}

fun loadContributorsBlocking(service: GitHubService, req: RequestData): List<User> {

    // repository 정보를 가져옴
    val repos = service
        .getOrgReposCall(req.org)
        .execute() // Executes request and blocks the current thread
        .also { logRepos(req, it) }
        .body() ?: emptyList()

    // 각 repository의 contributor 정보를 가져옴
    return repos.flatMap { repo ->
        service
            .getRepoContributorsCall(req.org, repo.name)
            .execute() // Executes request and blocks the current thread
            .also { logUsers(repo, it) }
            .bodyList()
    }.aggregate()
}

fun <T> Response<List<T>>.bodyList(): List<T> {
    return body() ?: emptyList()
}
```

### Task 1

- `aggregate()` : `List<User>`를 받아서
    - 같은 이름 (`login`)을 가진 `User`를 합침
    - 합친 후, `contributions`를 기준으로 내림차순 정렬

```kotlin
class AggregationKtTest {
    @Test
    fun testAggregation() {
        val actual = listOf(
            User("Alice", 1), User("Bob", 3),
            User("Alice", 2), User("Bob", 7),
            User("Charlie", 3), User("Alice", 5)
        ).aggregate()
        val expected = listOf(
            User("Bob", 10),
            User("Alice", 8),
            User("Charlie", 3)
        )
        Assert.assertEquals("Wrong result for 'aggregation'", expected, actual)
    }
}

fun List<User>.aggregate(): List<User> =
    groupBy { it.login }
        .map { (login, group) -> User(login, group.sumOf { it.contributions }) }
        .sortedByDescending { it.contributions }
```

### Callbacks

- callback : 비동기 작업이 완료되면 호출되는 함수
- callback 을 사용해서 스레드를 블룅하고, UI 멈춤 현상을 해결
- operation이 완료되고 코드를 즉시 호출하는 대신에, 콜백 (일반적으로 람다)으로 분리하여 호출자에게 전달

### Use a background thread

```kotlin
thread {
    loadContributorsBlocking(service, req)
}
```

![img_1.png](img_1.png)

````
fun loadContributorsBackground(
    service: GitHubService, req: RequestData,
    updateResults: (List<User>) -> Unit
)

...

fun loadContributorsBackground(service: GitHubService, req: RequestData, updateResults: (List<User>) -> Unit) {
    thread {
        updateResults(loadContributorsBlocking(service, req))
    }
}

...

loadContributorsBackground(service, req) { users ->
    SwingUtilities.invokeLater {
        updateResults(users, startTime)
    }
}
````

- `updateResults()` : 콜백 함수, 모두 완료되면 호출
- `SwingUtilities.invokeLater` : UI 업데이트를 위해 사용

### Use the Retrofit callback API

- 순차적으로 loading request가 이루어지는 문제점 해결하기
- loading 결과가 나올때까지 분리된 thread가 blocking 되는 문제 해결하기
- loading + processing 중 processing을 callback으로 분리하기

![img_2.png](img_2.png)

- Retrofit callback API 의 `Call.enqueue()` 로 HTPP reuqest에 대한 callback을 등록할 수 있다.

```kotlin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

fun loadContributorsCallbacks(service: GitHubService, req: RequestData, updateResults: (List<User>) -> Unit) {
    service.getOrgReposCall(req.org).onResponse { responseRepos ->
        logRepos(req, responseRepos)
        val repos = responseRepos.bodyList()
        val allUsers = Collections.synchronizedList(mutableListOf<User>())
        val numberOfProcessed = AtomicInteger()
        for (repo in repos) {
            service.getRepoContributorsCall(req.org, repo.name)
                .onResponse { responseUsers ->
                    logUsers(repo, responseUsers)
                    val users = responseUsers.bodyList()
                    allUsers += users
                    if (numberOfProcessed.incrementAndGet() == repos.size) {
                        updateResults(allUsers.aggregate())
                    }
                }
        }
    }
}
```

- `onResponse` : `Call`에 대한 callback을 등록하는 extension function

### Suspending functions

- `Call<List<Repo>>` 을 리턴하는 대신 `suspend` function을 사용

```kotlin
interface GitHubService {
    @GET("orgs/{org}/repos?per_page=100")
    suspend fun getOrgRepos(
        @Path("org") org: String,
    ): List<Repo>
}
````

- `getOrgRepos()` 은 suspend funciton
    - reqeust 스레드는 block 되지 않음
- `getOrgRepos()` 은 `List<Repo>`를 리턴 (Call<List<Repo>> 대신)

```kotlin

import retrofit2.Response

interface GitHubService {
    // getOrgReposCall & getRepoContributorsCall declarations

    @GET("orgs/{org}/repos?per_page=100")
    suspend fun getOrgRepos(
        @Path("org") org: String,
    ): Response<List<Repo>>

    @GET("repos/{owner}/{repo}/contributors?per_page=100")
    suspend fun getRepoContributors(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): Response<List<User>>
}
```

- retrofit을 사용하여 `Response`를 리턴하는 suspend function을 작성, 대체 가능

```kotlin
suspend fun loadContributorsSuspend(service: GitHubService, req: RequestData): List<User> {
    val repos = service
        .getOrgRepos(req.org) // suspend function
        .also { logRepos(req, it) }
        .body() ?: emptyList()

    return repos.flatMap { repo ->
        service
            .getRepoContributors(req.org, repo.name) // suspend function
            .also { logUsers(repo, it) }
            .bodyList()
    }.aggregate()
}

```

### Coroutines

- threa가 blocking 되는 것은 coroutine이 suspend되는 것과 비슷한 개념
- 코루틴은 경량 스레드라고도 불림 (lightweight thread)

| thread | coroutine |
|--------|-----------|
| block  | supdend   |

#### Starting a new coroutine

```kotlin
// 아래는 하나의 코루틴 @coroutine#1에서 실행됨
// 모든 contributor를 load하고,
// 결과를 updateResults()로 전달
launch {
    val users = loadContributorsSuspend(req)
    updateResults(users, startTime)
}
```

- `launch` : 새로운 computation 시작 (새로운 코루틴 시작)
    - compuation은 suspendable
    - network rquest 시 suspend되어 thread를 release
    - network result가 오면 다시 resume
- **coroutine** : suspendable computation
- 코루틴은 스레드 위에서 실행되고, suspend됨
    - suspended : computation이 일시중지, thread에서 제어권을 양보, 메모리에 저장 => thread는 다른 태스크를 수행
- computation이 다시 실행가능해지면
    - 스레드에 다시할당 (다른 스레드일수도 있음)
    - 코루틴은 오직 응답이 왔을떄만 재개 가능

![img_4.png](img_4.png)

### Concurrency

- 코틀린 코루틴은 스레드보다 덜 리소를 소모함
- 코루틴 생성 = 새로운 비동기 연산 실행
- **coroutine builder** : 새로운 코루틴을 시작 e.g. `launch`, `async`, `runBlocking`
- `async` : 새로운 코루틴을 싲가하고, `Deferred`를 반환
    - `Deferred` : `Future`, `Promise`와 비슷한 개념
        - 연산을 저장하고, **future** 에 결과를 반환할거라는 **promise**를 가짐
- `async`와 `launch`의 차이점
    - `launch` : 결과를 반환하지 않음
        - `Job`을 반환 (`Job` : 코루틴을 나타냄)
        - `Job.join()` : 코루틴이 완료될때까지 기다림
- `Deferred` : `Job`을 확장한 제네릭 타입
    - `async` 는 `Deferred<Int>`를 반환 (or `Deferred<CustomeType>`)
    - `Deferred.await()` : 코루틴 결과 반환, `await()`을 호출한 코루틴은 suspended

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    val deferred: Deferred<Int> = async {
        loadData()
    }
    println("waiting...")
    println(deferred.await())
}

suspend fun loadData(): Int {
    println("loading...")
    delay(1000L)
    println("loaded!")
    return 42
}
```

```
waiting...
loading...
loaded!
42

Process finished with exit code 0
```

- `runBlocking` : regular funciton과 suspending function의 브릿지
- https://youtu.be/zEZc5AmHQhk

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    val deferreds: List<Deferred<Int>> = (1..3).map {
        async {
            delay(1000L * it)
            println("Loading $it")
            it
        }
    }
    val sum = deferreds.awaitAll().sum()
    println("$sum")
}
```

![img_5.png](img_5.png)

````kotlin
suspend fun loadContributorsConcurrent(service: GitHubService, req: RequestData): List<User> = coroutineScope {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .bodyList()

    val deferreds: List<Deferred<List<User>>> = repos.map { repo ->
        // 메인스레드에서 코루틴 생성
        async {
            service.getRepoContributors(req.org, repo.name)
                .also { logUsers(repo, it) }
                .bodyList()
        }
    }
    deferreds.awaitAll().flatten().aggregate()
}

````

````
...
    async(Dispatchers.Default) {
        log("starting loading for ${repo.name}")
        service.getRepoContributors(req.org, repo.name)
            .also { logUsers(repo, it) }
            .bodyList()
    }
...
````

- `async(Dispatchers.Default) { }` : `async` 를 사용하여 새로운 코루틴을 시작하고, `Dispatchers.Default`를 사용하여 코루틴을 실행할 스레드를 지정
    - `CoroutineDispatcher` : 코루틴을 실행할 스레드를 지정
    - `Dispatchers.Default` : JVM의 스레드 shared pool

````
// 메인 스레드에서 코루틴 실행
launch(Dispatchers.Main) {
    updateResults()
}
````

- 메인 스레드가 바쁘면, 코루틴은 suspended

````
launch(Dispatchers.Default) {
    val users = loadContributorsConcurrent(service, req)
    withContext(Dispatchers.Main) {
        updateResults(users, startTime)
    }
}
````

- `updateResults`는 메인 스레드에서 실행되어야 하므로, `withContext(Dispatchers.Main)`을 사용하여 메인 스레드에서 실행
- `withContext()` : 람다를 특정한 coroutine context에서 실행
    - 완료될 떄까지 suspended
    - `launch(context) { ... }.join()` 과 같은 의미

### Structured concurrency

- **coroutine scope** :  코루틴간의 parent-child 관계, 구조에 대한 관리를 함
    - 새로운 코루틴은 코루틴 스코프에서 시작되어야함
- **coroutine context** : 코루틴이 실행되는 환경에 대한 추가적인 정보들 e.g. 코루틴 커스텀 이름, 스레드 특정 가능한 디스패쳐 등
- scope은 일반적으로 childe coroutine에 대한 책임이 있다.
- scope은 child coroutine을 취소시킬 수 있다
- scope은 child corouitine의 완료를 기다린다.
    - 따라서 scope에 있는 모든 코루틴이 완료되지 않으면 parent 코루틴은 완료될 수 없다

```kotlin
launch { /* this: CoroutineScope */ }
```

- `launch` 람다의 암묵적 receiver는 `CoroutineScope` 인터페이스
- runBlocking, launch, or async 안의 새로운 코루틴은 자동을 해당 scope에서 실행됨
- `runBlocking` 은 top-level function으로서 현제 스레드를 block

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking { /* this: CoroutineScope */
    launch { /* ... */ }
    // the same as:
    this.launch { /* ... */ }
}
```

- nested coroutine은 outer coroutne의 child로 취급
    - scope을 통해 parent-child 관계를 유지
- `coroutineScope()` : 새로운 scope를 만들기만 함 (코루틴 생성, 실행 없음)
- `Global.async()`, `GlobalScope.launch()` : global scope에서 코루틴 생성
    - top-level "independent" coroutines
    - 코루틴의 라이프타임 한계는 application 라이프 타임

#### Canceling the loading of contributors

```kotlin
suspend fun loadContributorsConcurrent(
    service: GitHubService,
    req: RequestData,
): List<User> = coroutineScope {
    // ...
    async {
        log("starting loading for ${repo.name}")
        delay(3000)
        // load repo contributors
    }
    // ...
}
```

```kotlin
interface Contributors {

    fun loadContributors() {
        // ...
        when (getSelectedVariant()) {
            CONCURRENT -> {
                launch {
                    val users = loadContributorsConcurrent(service, req)
                    updateResults(users, startTime)
                }.setUpCancellation()      // #1
            }
        }
    }

    private fun Job.setUpCancellation() {
        val loadingJob = this              // #2

        // cancel the loading job if the 'cancel' button was clicked:
        val listener = ActionListener {
            loadingJob.cancel()            // #3
            updateLoadingStatus(CANCELED)
        }
        // add a listener to the 'cancel' button:
        addCancelListener(listener)

        // update the status and remove the listener
        // after the loading job is completed
    }
}
```

- `#1` : `launch` 의 리턴값인 `Job`에 `setUpCancellation()`을 호출
    - `Job` 에는 loading 코루틴에 대한 참조를 가짐

````kotlin
suspend fun loadContributorsNotCancellable(
    service: GitHubService,
    req: RequestData,
): List<User> {   // #1
    // ...
    GlobalScope.async {   // #2
        log("starting loading for ${repo.name}")
        // load repo contributors
    }
    // ...
    return deferreds.awaitAll().flatten().aggregate()  // #3
}
````

- `loadContributorsNotCancellable` : 코루틴이 취소되지 않는다.
    - `GlobalScope.async` : global scope에서 코루틴 생성
    - `GlobalScope` : application life time
    - `GlobalScope`에서 생성된 코루틴은 application이 종료될때까지 실행

#### Using the outer scope's context

- 모든 nested 코루틴은 자동으로 inherited context (부모 코루틴의 context)에서 시작

```kotlin
launch(Dispatchers.Default) {  // outer scope
    val users = loadContributorsConcurrent(service, req)
    // ...
}
````

```kotlin
suspend fun loadContributorsConcurrent(
    service: GitHubService, req: RequestData,
): List<User> = coroutineScope {
    // this scope inherits the context from the outer scope
    // ...
    async {   // nested coroutine started with the inherited context
        // ...
    }
    // ...
}
```

### Showing progress

- repo마다 데이터가 로딩되자마자 UI에 표시

```kotlin
suspend fun loadContributorsProgress(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .bodyList()

    var allUsers = emptyList<User>()
    for ((index, repo) in repos.withIndex()) {
        val users = service.getRepoContributors(req.org, repo.name)
            .also { logUsers(repo, it) }
            .bodyList()

        allUsers = (allUsers + users).aggregate()
        updateResults(allUsers, index == repos.lastIndex)
    }
}

launch(Dispatchers.Default) {
    loadContributorsProgress(service, req) { users, completed ->
        withContext(Dispatchers.Main) {
            updateResults(users, startTime, completed)
        }
    }
}
```

- `updateResults()` : aggregate 중간마다 UI에 표시
    - `suspend` function으로 선언된 파라미터
    - `withContext` : lamda 블록을 다른 context (`loadContributorsProgress`) 에서 실행

#### Consecutive vs concurrent

![img_6.png](img_6.png)

- Consecutive (sequential) : repo를 순차적으로 접근해서 aggregate
- 동기화 필요 없음

![img_7.png](img_7.png)

- Concurrent : repo를 순차적으로 접근하지만, aggregate를 concurrent하게 실행
- 동기화 필요 (synchronization)
    - 동기화 작업 : `updateResults()` 내부에서 수행

### Channels

![img_8.png](img_8.png)

- 코루틴 간에 데이터를 통신(전달) 하는 방법
- 코루틴 하나가 채널로 정보를 보내면, 다른 하나가 받을 수 있음

![img_9.png](img_9.png)

- producer-consumer 패턴 가능
- N개의 코루틴이 같은 채널을 통해 데이터를 받을 때, 채널로부터 데이터가 처리된 (consume) 즉시 el은 채널에서 제거됨
- **suspend** `send()`, `receive()` 함수를 사용하여 채널을 통해 데이터를 보내고 받음
- 채널의 크기는 유한할때, 채널의 크기가 가득 차면 `send()` 함수는 suspended 됨

```kotlin
interface SendChannel<in E> {
    suspend fun send(element: E)
    fun close(): Boolean
}

interface ReceiveChannel<out E> {
    suspend fun receive(): E
}

interface Channel<E> : SendChannel<E>, ReceiveChannel<E>
```

- `Channel`의 세가지 인터페이스
    - `SendChannel` : 데이터를 보내는 채널
    - `ReceiveChannel` : 데이터를 받는 채널
    - `Channel` : 데이터를 보내고 받는 채널

![img_10.png](img_10.png)

- Unlimited channel : 큐와 가장 비슷
- 프로듀서가 무한정으로 채널에 데이터를 보낼 수 있음
- `send()` 는 suspended 되지 않음
- 빈 채널에 `receive()` 를 호출하면 suspended 됨

![img_11.png](img_11.png)

- Buffer channel : 크기가 유한한 채널
- 채널이 가득 차면 `send()` 는 suspended 됨

![img_12.png](img_12.png)

- Rendezvous channel : 크기가 0인 채널
- `send()`, `receive()` 는 항상 suspended 됨 (둘 다 준비될 때까지)

- Conflated channel : 가장 최근에 보낸 데이터만 유지
- `send()` 시 채널이 가득 차면, 가장 최근에 보낸 데이터로 채널을 덮어씀 (suspend 되지 않음)
- `receive()` 는 항상 가장 최근에 보낸 데이터를 받음

```kotlin
val rendezvousChannel = Channel<String>() // Rendezvous channel (default)
val bufferedChannel = Channel<String>(10) // Buffered channel with buffer size 10
val conflatedChannel = Channel<String>(CONFLATED) // Conflated channel
val unlimitedChannel = Channel<String>(UNLIMITED) // Unlimited channel
```

```kotlin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    val channel = Channel<String>()
    launch {
        channel.send("A1")
        channel.send("A2")
        log("A done")
    }

    launch {
        channel.send("B1")
        log("B done")
    }

    launch {
        repeat(3) {
            log(channel.receive())
        }
    }
}


fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")
```

````
[main] A1
[main] B1
[main] A done
[main] B done
[main] A2

Process finished with exit code 0
````

````kotlin
suspend fun loadContributorsChannels(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit,
) {
    coroutineScope {
        val repos = service
            .getOrgRepos(req.org)
            .also { logRepos(req, it) }
            .bodyList()

        val channel = Channel<List<User>>()
        for (repo in repos) {
            launch {
                val users = service.getRepoContributors(req.org, repo.name)
                    .also { logUsers(repo, it) }
                    .bodyList()
                channel.send(users) // send data to the channel
            }
        }
        var allUsers = emptyList<User>()
        repeat(repos.size) {
            val users = channel.receive()
            allUsers = (allUsers + users).aggregate()
            updateResults(allUsers, it == repos.lastIndex)
        }
    }
}
````

### Testing coroutines

- 실행시간을 직접 측정하면 machine 준비시간 등이 합쳐져 부정확함
- test dispatcher 사용해서 virtual time을 측정
- `runTest` : test dispatcher를 사용하여 코루틴을 실행 (`runBlocking`과 비슷)
- test dispatcher에서 `delay` 하면 즉시 리턴하고 virtual time을 증가시킴

![img_14.png](img_14.png)

````kotlin
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class TestingCoroutines {
    @Test
    fun testDelayInSuspend() = runTest {
        val realStartTime = System.currentTimeMillis()
        val virtualStartTime = currentTime

        foo()
        println("${System.currentTimeMillis() - realStartTime} ms") // ~ 6 ms
        println("${currentTime - virtualStartTime} ms")             // 1000 ms
    }

    suspend fun foo() {
        delay(1000)    // auto-advances without delay
        println("foo") // executes eagerly when foo() is called
    }

}

````

````
foo
0 ms
1000 ms

Process finished with exit code 0
````

```kotlin
@Test
fun testDelayInLaunch() = runTest {
        val realStartTime = System.currentTimeMillis()
        val virtualStartTime = currentTime

        bar()

        println("${System.currentTimeMillis() - realStartTime} ms") // ~ 11 ms
        println("${currentTime - virtualStartTime} ms")             // 1000 ms
    }

suspend fun bar() = coroutineScope {
    launch {
        delay(1000)    // auto-advances without delay
        println("bar") // executes eagerly when bar() is called
    }
}
```

````
bar
1 ms
1000 ms

Process finished with exit code 0
````

- `delay` 가 있음에도 불구하고, 실제 실행시간이 delay 되지 않음

```kotlin

@Test
fun test() = runTest {
    val startTime = currentTime
    // action
    val totalTime = currentTime - startTime
    // testing result
}


fun testConcurrent() = runTest {
    val startTime = currentTime
    val result = loadContributorsConcurrent(MockGithubService, testRequestData)
    Assert.assertEquals("Wrong result for 'loadContributorsConcurrent'", expectedConcurrentResults.users, result)
    val totalTime = currentTime - startTime

    Assert.assertEquals(
        "The calls run concurrently, so the total virtual time should be 2200 ms: " +
                "1000 for repos request plus max(1000, 1200, 800) = 1200 for concurrent contributors requests)",
        expectedConcurrentResults.timeFromStart, totalTime
    )
}

fun testChannels() = runTest {
    val startTime = currentTime
    var index = 0
    loadContributorsChannels(MockGithubService, testRequestData) { users, _ ->
        val expected = concurrentProgressResults[index++]
        val time = currentTime - startTime
        Assert.assertEquals(
            "Expected intermediate results after ${expected.timeFromStart} ms:",
            expected.timeFromStart, time
        )
        Assert.assertEquals("Wrong intermediate results after $time:", expected.users, users)
    }
}
```

## Cancellation and timeouts

### Cancelling coroutine execution

- 코루틴을 취소할 니즈가 있음 (유저가 UI를 닫았는데, 코루틴이 계속 실행되는 경우)

````kotlin
val job = launch {
    repeat(1000) { i ->
        println("job: I'm sleeping $i ...")
        delay(500L)
    }
}
delay(1300L) // delay a bit
println("main: I'm tired of waiting!")
job.cancel() // cancels the job
job.join() // waits for job's completion 
println("main: Now I can quit.")
````

````
job: I'm sleeping 0 ...
job: I'm sleeping 1 ...
job: I'm sleeping 2 ...
main: I'm tired of waiting!
main: Now I can quit.
````

- `launch` 가 반환하는 `Job`을 사용하여 코루틴을 취소

### Cancellation is cooperative

- 코루틴은 cooperative하게 취소됨
- `kotlinx.coroutines` 의 모든 suspeding function은 취소 가능
- 코루틴 취소를 체크하다 `CancellationException`을 던지고, 취소시킴
- 코루틴 연산을 하고있어서, 취소 체크를 안하고 있다면 취소 불가능

````kotlin
val startTime = System.currentTimeMillis()
val job = launch(Dispatchers.Default) {
    var nextPrintTime = startTime
    var i = 0
    while (i < 5) { // computation loop, just wastes CPU
        // print a message twice a second
        if (System.currentTimeMillis() >= nextPrintTime) {
            println("job: I'm sleeping ${i++} ...")
            nextPrintTime += 500L
        }
    }
}
delay(1300L) // delay a bit
println("main: I'm tired of waiting!")
job.cancelAndJoin() // cancels the job and waits for its completion
println("main: Now I can quit.")
````

````
job: I'm sleeping 0 ...
job: I'm sleeping 1 ...
job: I'm sleeping 2 ...
main: I'm tired of waiting!
job: I'm sleeping 3 ...
job: I'm sleeping 4 ...
main: Now I can quit.
````

- `job.cancelAndJoin()` : `cancel()`과 `join()`을 합친 함수
- "I'm sleeping" 일때 취소가 불가능함

```kotlin
val job = launch(Dispatchers.Default) {
    repeat(5) { i ->
        try {
            // print a message twice a second
            println("job: I'm sleeping $i ...")
            delay(500)
        } catch (e: Exception) {
            // log the exception
            println(e)
        }
    }
}
delay(1300L) // delay a bit
println("main: I'm tired of waiting!")
job.cancelAndJoin() // cancels the job and waits for its completion
println("main: Now I can quit.")
```

- `try-catch` 블록을 사용하여 `CancellationException`을 catch하면서 취소가 불가능해짐

### Making computation code cancellable

- 취소 가능한 코드를 작성하는 방법
- 방법 1. 주기적으로 cancellation check
    - `yield()` 를 활용
- 방법 2. 명시적으로 취소 상태를 확인

```kotlin
val startTime = System.currentTimeMillis()
val job = launch(Dispatchers.Default) {
    var nextPrintTime = startTime
    var i = 0
    while (isActive) { // cancellable computation loop
        // print a message twice a second
        if (System.currentTimeMillis() >= nextPrintTime) {
            println("job: I'm sleeping ${i++} ...")
            nextPrintTime += 500L
        }
    }
}
delay(1300L) // delay a bit
println("main: I'm tired of waiting!")
job.cancelAndJoin() // cancels the job and waits for its completion
println("main: Now I can quit.")
```

````
job: I'm sleeping 0 ...
job: I'm sleeping 1 ...
job: I'm sleeping 2 ...
main: I'm tired of waiting!
main: Now I can quit.
````

- `isActive` : `CoroutineScope`의 확장 프로퍼티

### Closing resources with finally

- suspending funciton은 취소되었을때 `CancellationException`을 던짐
- `finally` 블록을 사용하여 취소되었을때 리소스를 해제

```kotlin
val job = launch {
    try {
        repeat(1000) { i ->
            println("job: I'm sleeping $i ...")
            delay(500L)
        }
    } finally {
        println("job: I'm running finally")
    }
}
delay(1300L) // delay a bit
println("main: I'm tired of waiting!")
job.cancelAndJoin() // cancels the job and waits for its completion
println("main: Now I can quit.")
````

````
job: I'm sleeping 0 ...
job: I'm sleeping 1 ...
job: I'm sleeping 2 ...
main: I'm tired of waiting!
job: I'm running finally
main: Now I can quit.
````

### Run non-cancellable block

- `withContext(NonCancellable)` : 취소 불가능한 블록을 실행
- 아주 드문 케이스로 취소 불가능한 블록을 실행해야할때 사용

```kotlin
val job = launch {
    try {
        repeat(1000) { i ->
            println("job: I'm sleeping $i ...")
            delay(500L)
        }
    } finally {
        withContext(NonCancellable) {
            println("job: I'm running finally")
            delay(1000L)
            println("job: And I've just delayed for 1 sec because I'm non-cancellable")
        }
    }
}
delay(1300L) // delay a bit
println("main: I'm tired of waiting!")
job.cancelAndJoin() // cancels the job and waits for its completion
println("main: Now I can quit.")
```

```
job: I'm sleeping 0 ...
job: I'm sleeping 1 ...
job: I'm sleeping 2 ...
main: I'm tired of waiting!
job: I'm running finally
job: And I've just delayed for 1 sec because I'm non-cancellable
main: Now I can quit.
```

### Timeout

- 실무에서 코루틴을 취소해야하는 이유 중 가장 큰 부분
- 코루틴의 실행이 특정 시간을 초과하면 취소
- `withTimeout` : 특정 시간을 초과하면 `TimeoutCancellationException`을 던짐
    - `TimeoutCancellationException` 은 `CancellationException`의 하위 클래스
    - `CancellationException`은 보통 완료로 처리하지만, `withTimeout` 안에서는 예외로 처리
- `withTimeoutOrNull` : `TimeoutCancellationException`을 던지지 않고 `null`을 반환

```kotlin
withTimeout(1300L) {
    repeat(1000) { i ->
        println("I'm sleeping $i ...")
        delay(500L)
    }
}
```

````
'm sleeping 0 ...
I'm sleeping 1 ...
I'm sleeping 2 ...
Exception in thread "main" kotlinx.coroutines.TimeoutCancellationException: Timed out waiting for 1300 ms
 at _COROUTINE._BOUNDARY._ (CoroutineDebugging.kt:46) 
 at FileKt$main$1$1.invokeSuspend (File.kt:-1) 
 at FileKt$main$1.invokeSuspend (File.kt:-1) 
Caused by: kotlinx.coroutines.TimeoutCancellationException: Timed out waiting for 1300 ms
at kotlinx.coroutines.TimeoutKt .TimeoutCancellationException(Timeout.kt:191)
````

````kotlin
val result = withTimeoutOrNull(1300L) {
    repeat(1000) { i ->
        println("I'm sleeping $i ...")
        delay(500L)
    }
    "Done" // will get cancelled before it produces this result
}
println("Result is $result")
````

````
I'm sleeping 0 ...
I'm sleeping 1 ...
I'm sleeping 2 ...
Result is null
````

### Asynchronous timeout and resources

- `withTimeout` 을 사용했고, 블록 내에서 리소스를 사용하는 경우 블록 밖에서 리소스를 해제
- 아래 코드에서 리소스 누수는 없음

```kotlin
var acquired = 0

class Resource {
    init {
        acquired++
    } // Acquire the resource

    fun close() {
        acquired--
    } // Release the resource
}

fun main() {
    runBlocking {
        repeat(10_000) { // Launch 10K coroutines
            launch {
                val resource = withTimeout(60) { // Timeout of 60 ms
                    delay(50) // Delay for 50 ms
                    Resource() // Acquire a resource and return it from withTimeout block     
                }
                resource.close() // Release the resource
            }
        }
    }
    // Outside of runBlocking all coroutines have completed
    println(acquired) // Print the number of resources still acquired
}
```

```
0
```

````kotlin
runBlocking {
    repeat(10_000) { // Launch 10K coroutines
        launch {
            var resource: Resource? = null // Not acquired yet
            try {
                withTimeout(60) { // Timeout of 60 ms
                    delay(50) // Delay for 50 ms
                    resource = Resource() // Store a resource to the variable if acquired      
                }
                // We can do something else with the resource here
            } finally {
                resource?.close() // Release the resource if it was acquired
            }
        }
    }
}
// Outside of runBlocking all coroutines have completed
println(acquired) // Print the number of resources still acquired
````

```
0
```

## Composing suspending functions

suspending function을 조합하는 여러 방법

### Sequential by default

```kotlin
suspend fun doSomethingUsefulOne(): Int {
    delay(1000L) // pretend we are doing something useful here
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    delay(1000L) // pretend we are doing something useful here, too
    return 29
}

fun main() {
    val time = measureTimeMillis {
        val one = doSomethingUsefulOne()
        val two = doSomethingUsefulTwo()
        println("The answer is ${one + two}")
    }
    println("Completed in $time ms")
}
```

````
The answer is 42
Completed in 2017 ms
````

- 코루틴의 코드는 기본적으로 regular code처럼 sequential하게 실행됨

### Concurrent using async

- 각 suspending function을 `async`로 감싸면 concurrent하게 실행됨
- `async` : `launch`와 비슷
    - `launch` : 결과를 반환하지 않음
    - `async` : 결과 `Deferred`를 반환
- `Deferred` : light-weight non-blocking future
    - `Deferred.await()` : 결과를 반환
    - `Job` 처럼 필요시 cancel 가능

```kotlin
val time = measureTimeMillis {
    val one = async { doSomethingUsefulOne() }
    val two = async { doSomethingUsefulTwo() }
    println("The answer is ${one.await() + two.await()}")
}
println("Completed in $time ms")
```

````
The answer is 42
Completed in 1017 ms
````

### Lazily started async

- `async`는 `start` 파라미터를 가짐
- `start` : `CoroutineStart` 타입
    - `DEFAULT` : 즉시 시작
    - `LAZY` : `Deferred`를 반환하고, `Deferred.await()` 호출 혹은 `Job.start()` 호출시 시작

```kotlin
val time = measureTimeMillis {
    val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
    val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
    // some computation
    one.start() // start the first one
    two.start() // start the second one
    println("The answer is ${one.await() + two.await()}")
}
println("Completed in $time ms")
```

````
The answer is 42
Completed in 1017 ms
````

## Coroutine context and dispatchers

## Asynchronous Flow

## Channels

## Coroutine exception handling

## Shared mutuable state and concurrency

## Select expression (experimental)

## Debug coroutines using IntelliJ IDEA - tutorial

## Debug Kotlin Flow using IntelliJ IDEA - tutorial
