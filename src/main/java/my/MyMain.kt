package my

object MyMain {

    @JvmStatic
    fun main(args: Array<String>) {
        println("it works")
    }
}

data class User(val name: String, val skills: Map<String, Long>)

data class Project(val days: Long, val score: Long, val bestBefore: Long, val roleToLevel: Map<String, Long>)
