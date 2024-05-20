package coroutines

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * @author gihyeon-kim
 */


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
            log("received: ${channel.receive()}")
        }
    }
}


fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")
