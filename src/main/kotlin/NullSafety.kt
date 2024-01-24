/**
 * null safety : null value의 문제를 compile-time에 탐지 (runtime error 방지)
 *
 * 방법 1. 명시적으로 null value 허용
 * 방법 2. null value 체크
 * 방법 3. null value 일수도 있는 properties, function을 safe call <br>
 * 방법 4. null value를 마주했을 때 취할 행동 선언
 *
 */
fun main() {
    // nullable types : ?
    // default로 null 허용 안함

    // neverNull
    var neverNull: String = "This can't be null"

    // compile error : Null can not be a value of a non-null type String
    // neverNull = null

    var nullable: String? = "You can keep a null here"
    nullable = null

    var infereedNonNull = "The compiler assumes non-null"

    // compile error : Null can not be a value of a non-null type String
    // infereedNonNull = null

    fun strLength(notNull: String): Int {
        return notNull.length
    }

    println(strLength(neverNull))
    // compile error : Null can not be a value of a non-null type String
    // println(strLength(nullable))

    // Check for null values
    val nullStr1: String? = null
    println(describeStr(nullStr1))

    val nullStr2: String? = null
    println(lengthStr(nullStr2))

    var nullStr3: String? = null
    println(nullStr3?.uppercase())

    // Elvis operator ?:
    // null value일 때 default value를 사용하고 싶을 때
    var nullStr4: String? = null
    println(nullStr4?.uppercase() ?: "this is null value")

}

fun describeStr(maybeStr: String?): String {
    if (maybeStr != null && maybeStr.length > 0) {
        return "String of length ${maybeStr.length}"
    } else {
        return "Empty or null string"
    }
}

// Use safe calls
// ?. 를 사용해서 null value 다루기
fun lengthStr(maybeStr: String?): Int? = maybeStr?.length


