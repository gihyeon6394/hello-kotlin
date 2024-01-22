/**
 * Data class : data 저장 시 유용한 class
 * member function 자동 생성
 * member function : toString(), equals() or ==, copy()
 */
data class User(val name: String, val id: Int) {

}

fun main() {
    val user = User("Alex", 1)
    println(user) // toString() 자동 생성

    val userSecond = User("Alex", 1)
    val userThird = User("Max", 2)

    println("user == secondUser : ${user == userSecond}") // true
    println("user == thirdUser : ${user == userThird}") // false

    // copy instance
    val userCopied = user.copy()
    println("userCopied : $userCopied")

    println("userCopied (with new id) : ${user.copy(id = 3)}")
}
