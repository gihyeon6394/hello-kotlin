package coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

/**
 * @author gihyeon-kim
 */

suspend fun main() {
//    val nums = (1..3).asFlow()
//    val strs = flowOf("one", "two", "three")
//    nums.zip(strs){ a, b -> "$a -> $b" }.collect { println(it)}

    val nums1 = (1..3).asFlow().onEach { delay(300) } // numbers 1..3 every 300 ms
    val strs1 = flowOf("one", "two", "three").onEach { delay(400) } // strings every 400 ms
    val startTime1 = System.currentTimeMillis() // remember the start time
    nums1.zip(strs1) { a, b -> "$a -> $b" } // compose a single string with "zip"
        .collect { value -> // collect and print
            println("$value at ${System.currentTimeMillis() - startTime1} ms from start")
        }

    val nums2 = (1..3).asFlow().onEach { delay(300) } // numbers 1..3 every 300 ms
    val strs2 = flowOf("one", "two", "three").onEach { delay(400) } // strings every 400 ms
    val startTime2 = System.currentTimeMillis() // remember the start time
    nums2.combine(strs2) { a, b -> "$a -> $b" } // compose a single string with "combine"
        .collect { value -> // collect and print
            println("$value at ${System.currentTimeMillis() - startTime2} ms from start")
        }
}
