# Coroutines (kotlinx.coroutines)

- Coroutines basics
- Coroutines and channels-tutorial
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

### Task 3 (optional)

### Suspending functions

### Coroutines

### Concurrency

### Structured concurrency

### Showing progress

### Channels

### Testing coroutines

## Composing suspending functions

## Coroutine context and dispatchers

## Asynchronous Flow

## Channels

## Coroutine exception handling

## Shared mutuable state and concurrency

## Select expression (experimental)

## Debug coroutines using IntelliJ IDEA - tutorial

## Debug Kotlin Flow using IntelliJ IDEA - tutorial
