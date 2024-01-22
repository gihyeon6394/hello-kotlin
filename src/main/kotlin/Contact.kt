class Contact(val id: Int, var email: String) { // class header
    val category: String = ""
    // val 을 사용해서 불변하게 필드를 선언하기를 추천
    // 불변 : instance 생성 후에는 값이 바뀔수 없음

    // Member function
    fun printId(){
        println("The id is $id")
    }
}

// create instance

fun main() {

    // concat : instance
    // id, email : properties
    val contact = Contact(1, "example123@example.com")

    // Access properties
    println(contact.email)

    contact.email = "example456@example.com"
    println(contact.email)
    println("The email addr is ${contact.email}")

    contact.printId()

}
