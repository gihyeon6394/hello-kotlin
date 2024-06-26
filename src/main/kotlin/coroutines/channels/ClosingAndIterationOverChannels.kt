package coroutines.channels

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * @author gihyeon-kim
 */
fun main() = runBlocking {
    val channel = Channel<Int>()
    launch {
        for (x in 1..5) channel.send(x * x)
        channel.close() // we're done sending
    }
// here we print received values using `for` loop (until the channel is closed)
    for (y in channel) println(y)
    println("Done!")
}
