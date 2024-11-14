package coroutines.myself.af

/**
 * @author gihyeon-kim
 */

fun main() {
    simple().forEach { value -> println(value) }
}

fun simple(): Sequence<Int> = sequence { // sequence builder
    for (i in 1..3) {
        Thread.sleep(1000) // pretend we are computing it
        yield(i) // yield next value
    }
}
