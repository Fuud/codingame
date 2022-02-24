package my

import java.io.File

data class Score(val taskName: String) {

    data class ProjectOut(val project: Project, val users: List<User>) {
    }

    fun print(projectOutList: List<ProjectOut>) {
        var score = 0L
        val user2day = mutableMapOf<User, Long/*available day*/>()

        projectOutList.forEach { it ->
            // find day to start project
            val day2start = it.users.map { u -> user2day[u] ?: 0 }.max() ?: 0
            val endDay = day2start + it.project.bestBefore
            if (endDay < it.project.bestBefore) {
                score += it.project.score
            } else {
                val scoreAdd = it.project.bestBefore - (endDay + 1 - it.project.bestBefore)
                if (scoreAdd > 0) {
                    score += scoreAdd
                }
            }
            it.users.forEach { user -> user2day[user] = endDay }
        }

        File("${taskName}.${score}.${System.currentTimeMillis()}.txt").printWriter().use { writer ->
            writer.println(projectOutList.size)
            projectOutList.forEach {
                writer.println(it.project.name)
                val userString = it.users.joinToString(separator = " ") { u -> u.name }
                writer.print(userString)
            }
        }
    }
}