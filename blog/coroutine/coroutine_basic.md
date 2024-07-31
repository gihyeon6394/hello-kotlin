# 코루틴 톺아보기

- 코루틴?

---

## 코루틴?

비동기, non-blocking 컨셉 개발은 너무 중요한 부분이다. 코틀린은 coroutine (코루틴) 라이브러리로 비동기 프로그래밍을 API 수준으로 쉽게 구현할 수 있게 했다.

### 기본 가이드

![img.png](img.png)

- `kotlinx.coroutines` 패키지가 코루틴을 사용하기위한 라이브러리로 JetBrains에서 개발했다.
    - `launch`, `asynce` 와 같은 고수준라이브러리를 제공해 편리하게 사용할 수 있다.
- `kotlinx-coroutines-core` 라이브러리를 임포트하면 코루틴을 사용할 수 있다.
    - https://github.com/Kotlin/kotlinx.coroutines/blob/master/README.md#maven

### 코루틴 시작해보기

- 코루틴은 중지가능한 (suspendable) 연산을 진행하는 인스턴스이다.
- 스레드와 비슷하게 실행중인 코드를 blocking할 수 있지만,
- 특정 스레드에서 중지된 코루틴이 다른 스레드에서 계속할 수 있다.
    - 즉 코루틴은 특정 스레드에 바인딩되지 않는다.

```kotlin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking { // runBlocking : 코루틴을 실행하고 완료될 때까지 현재 스레드를 blocking
    launch { // 코루틴 빌더 : 새로운 코루틴을 시작하고 실행
        delay(1000L) // 코루틴 suspend function : 코루틴을 일정 시간동안 중지
        println("World!")
    }

    println("Hello,")
}
```

````
Hello,
World!
````

1. `runBlocking{}` 으로 코루틴 스코프 생성, 코루틴 A 생성
2. `launch{}`로 새로운 코루틴 B 생성
3. `delay(1000L)`로 코루틴 B 1초동안 중지
4. 코루틴 B가 중지된 동안 코루틴 A가 실행되어 `Hello,` 출력 후 코루틴 B 기다림
5. 코루틴 B가 1초 후에 실행되어 `World!` 출력
6. 코루틴 B 종료
7. 코루틴 A 종료, `runBlocking{}` 종료, `main()` 종료


- `runBlocking`은 코루틴 빌더 (새로운 코루틴을 생성하는 함수)로서 빌더 중에서도 코루틴이 아닌 세상과 코루틴을 연결하는 빌더다.
- 위에서 main()을 실행한 스레드는 runBlocking 빌더로 코루틴을 생성하여 main을 실행하고 `main()`이 종료될때까지 blocking한다.

### Structured Concurrency (구조적 동시성)

- 코루틴은 Structured Concurrency를 따른다.
- Structured Concurrency 이란 새로운 코루틴은 반드시 `CoroutineScope` 안에서만 생성될 수 있다는 것으로 코루틴의 범위를 제한하는 메커니즘이다.
- 위에서 `runBlocking` 을 통해 코루틴 생성과 동시에 새로운 코루틴 범위 (스코프)를 생성했고, 그 스코프 안에서 `launch`를 통해 새로운 코루틴을 생성했다.

## Scope builder

- `CoroutineScope` 는 코루틴 범위 (스코프)를 나타내는 인터페이스로, 코루틴 범위를 생성하고 취소할 수 있는 메소드를 제공한다.
- `coroutineScope{}` 빌더는 코루틴 스코프를 생성하고 해당 스코프 안에서 생성 (런치)된 모든 코루틴이 완료될 때까지 기다린다.

### `runBlocking{}` vs `coroutineScope{}`

- 두 빌더 모두 body의 모든 코루틴과 코드가 종료될때까지 blocking된다는 공통점이 있다.
- 차이점은 `runBlocking{}` 은 현재 실행중인 스레드를 blocking하고 코루틴을 실행하는 것이고,
- `coroutineScope{}` 는 suspending function이다.
    - `coroutineScope{}`을 실행한 코루틴은 중지되고, 현재 스레드는 release한다.

```kotlin
// runBlocking
fun main() = runBlocking {
        launch {
            delay(1000L)
            println("World!")
        }

        println("Hello,")
    }

// coroutineScope
fun main() = runBlocking {
    coroutineScope {
        launch {
            delay(1000L)
            println("World!")
        }
    }
    println("Hello,")
}
```

- 아래 coroutineScope 예제는 `World!` 출력 후 `Hello,`가 출력된다.
- 아래처럼 실행된다.

1. main() 실행, runBlocking 빌더로 코루틴 A 생성 (main 스레드 blocking)
2. coroutineScope 빌더로 코루틴 스코프 생성 (여기서 코루틴 A는 중지되어 해당 스코프가 종료될때까지 기다림)
3. 해당 스코프에서 코루틴 B 생성
4. 코루틴 B 1초동안 중지 후 `World!` 출력
5. 코루틴 B 종료, 코루틴 스코프 종료
6. 코루틴 A `Hello,` 출력 후 종료, main() 종료

똑같이 출력되게 하려면 아래처럼 수정하면 된다.

```kotlin
fun main() = runBlocking {
    coroutineScope {
        launch {
            delay(1000L)
            println("World!")
        }
        println("Hello,")
    }
}
```
