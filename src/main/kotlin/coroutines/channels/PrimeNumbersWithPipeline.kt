package coroutines.channels

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking
import kotlin.text.Typography.prime

/**
 * @author gihyeon-kim
 */

// producer : produces a sequence of numbers starting from start
fun CoroutineScope.numbersFrom(start: Int) = produce {
    var x = start
    while (true) send(x++) // infinite stream of integers from start
}

// filter : filters out numbers not divisible by prime
fun CoroutineScope.filter(numbers: ReceiveChannel<Int>, prime: Int) = produce {
    for (x in numbers) if (x % prime != 0) send(x)
}


fun main() = runBlocking {
    var cur = numbersFrom(2)
    repeat(10) {
        val prime = cur.receive()
        println(prime)
        cur = filter(cur, prime)
    }
    coroutineContext.cancelChildren() // cancel all children to let main finish

    var curIterator = numbersFromSequence(2)
    repeat(10) {
        val prime = curIterator.next()
        println(prime)
        curIterator = filterBySequence(curIterator, prime)
    }
}


fun CoroutineScope.numbersFromSequence(start: Int) = iterator {
    var x = start
    while (true) yield(x++) // infinite stream of integers from start
}

fun CoroutineScope.filterBySequence(numbers: Iterator<Int>, prime: Int) = iterator {
    for (x in numbers) if (x % prime != 0) yield(x)
}

