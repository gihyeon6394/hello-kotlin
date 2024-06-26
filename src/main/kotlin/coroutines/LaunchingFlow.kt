package coroutines

import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

/**
 * @author gihyeon-kim
 */

fun events(): Flow<Int> = (1..3).asFlow().onEach { delay(100) }

fun foo(): Flow<Int> = flow {
    for (i in 1..5) {
        println("Emitting $i")
        emit(i)
    }
}

fun main() = runBlocking {
    events()
        .onEach { event -> println("Event: $event") }
        .collect()
    println("Done")

    events()
        .onEach { event -> println("Event: $event") }
        .launchIn(this) // <--- Launching the flow in a separate coroutine
    println("Done")

    foo().collect { value ->
        if (value == 3) cancel()
        println(value)
    }
}
