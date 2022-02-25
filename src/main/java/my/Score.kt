package my

import java.io.File

fun main(string: Array<String>) {
    val score = Score("A")
    val ann = User("Anna", mutableMapOf("C++" to 2))
    val bob = User("Bob", mutableMapOf("HTML" to 5, "CSS" to 5))
    val maria = User("Maria", mutableMapOf("Python" to 3))

    val logging = Project("Logging", 5, 10, 5, emptyList())
    val webServer = Project("WebServer", 7, 10, 7, emptyList())
    val webChat = Project("WebChat", 10, 20, 20, emptyList())
    score.print(projectOutList = listOf(Score.ProjectOut(webServer, listOf(bob, ann)),
        Score.ProjectOut(logging, listOf(ann)),
        Score.ProjectOut(webChat, listOf(maria, bob))
    ))
}

data class Score(val taskName: String) {

    data class ProjectOut(val project: Project, val users: List<User>) {
    }

    fun print(projectOutList: List<ProjectOut>) {
        var score = 0L
        val user2day = mutableMapOf<User, Long/*available day*/>()

        projectOutList.forEach { it ->
            // find day to start project
            val day2start = it.users.map { u -> user2day[u] ?: 0 }.maxOrNull() ?: 0
            val endDay = day2start + it.project.days - 1
            if (endDay < it.project.bestBefore) {
                score += it.project.score
            } else {
                val scoreAdd = it.project.score - (endDay + 1 - it.project.bestBefore)
                if (scoreAdd > 0) {
                    score += scoreAdd
                }
            }
            it.users.forEach { user -> user2day[user] = endDay + 1 }
        }

        println("${taskName}.${score}.txt")

        File("${taskName}.${score}.txt").printWriter().use { writer ->
            writer.println(projectOutList.size)
            projectOutList.forEach {
                writer.println(it.project.name)
                val userString = it.users.joinToString(separator = " ") { u -> u.name }
                writer.print(userString)
                writer.println()
            }
        }
    }

}