/**
 *
 *
 * Lists : ordered collection of el
 * Sets : unordered collection of unique el
 * Maps : collection of key-value pairs
 */
fun main() {

    // Lists
    val aespaReadOnly = listOf("Karina", "Giselle", "Ningning", "Winter")
    println("aespaReadOnly: $aespaReadOnly")

    val aespaMutable: MutableList<String> = mutableListOf("Karina", "Giselle", "Ningning", "Winter")
    println("aespaMutable: $aespaMutable")

    // casting : mutable to readonly
    val aespaLocked: List<String> = aespaMutable
    println("aespaLocked: $aespaLocked")
    println("aespaLocked[0]: ${aespaLocked[0]}")
    println("aespaLocked first el : ${aespaLocked.first()}")
    println("aespaLocked last el : ${aespaLocked.last()}")
    println("aespaLocked size : ${aespaLocked.size}")
    println("aespaLocked count : ${aespaLocked.count()}")

    aespaMutable.add("IU");
    println("aespaMutable: $aespaMutable")
    aespaMutable.remove("IU")
    println("aespaMutable: $aespaMutable")

    println()
    println("=====================================")
    println("=====================================")
    println()

    // Set
    val idolReadOnly = setOf("Aespa", "Blackpink", "Twice", "Red Velvet")
    println("idolReadOnly: $idolReadOnly")

    // Mutable
    val idolMutable: MutableSet<String> = mutableSetOf("Aespa", "Blackpink", "Twice", "Red Velvet")
    println("idolMutable: $idolMutable")

    // casting
    val idolLocked: Set<String> = idolMutable

    println("Aespa" in idolMutable)
    idolMutable.remove("Aespa")
    println("Aespa" in idolMutable)


    println()
    println("=====================================")
    println("=====================================")
    println()

    // Map
    val idolWithAgeReadOnly = mapOf(
        "Karina" to 23,
        "Giselle" to 23,
        "Ningning" to 18,
        "Winter" to 21
    )

    println("idolWithAgeReadOnly: $idolWithAgeReadOnly")

    val idolWithAgeMutable: MutableMap<String, Int> = mutableMapOf(
        "Karina" to 23,
        "Giselle" to 23,
        "Ningning" to 18,
        "Winter" to 21
    )

    println("idolWithAgeMutable: $idolWithAgeMutable")

    // casting
    val idolWithAgeLocked: Map<String, Int> = idolWithAgeMutable

    println("idoWithAgeReadOnly count: ${idolWithAgeReadOnly.count()}")
    println("Karina's age is ${idolWithAgeMutable["Karina"]}")

    idolWithAgeMutable.put("IU", 30)
    println("idolWithAgeMutable: $idolWithAgeMutable")

    idolWithAgeMutable.remove("IU")
    println("idolWithAgeMutable: $idolWithAgeMutable")
    println("there is IU? ${idolWithAgeMutable.containsKey("IU")}")

    println(idolWithAgeMutable.keys)
    println(idolWithAgeMutable.values)

    println("Kairina" in idolWithAgeMutable.keys)
    println(23 in idolWithAgeMutable.values)

    println()
    println("=====================================")
    println("=====================================")
    println()

    // Practice

    // Exercise 1
    val greenNumbers = listOf(1, 4, 23)
    val redNumbers = listOf(17, 2)
    val totalCount = greenNumbers.count() + redNumbers.count()
    println(totalCount)

    // Exercise 2
    val SUPPORTED = setOf("HTTP", "HTTPS", "FTP")
    val requested = "smtp"
    // val isSupported = SUPPORTED.contains(requested.uppercase())
    val isSupported = requested.uppercase() in SUPPORTED
    println("Support for $requested: $isSupported")

    // Exercise 3
    val number2word = mapOf(
        1 to "one",
        2 to "two",
        3 to "three"
    )
    val n = 2
    println("$n is spelt as ${number2word[n]}")


}