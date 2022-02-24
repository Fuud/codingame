package my

import java.io.File

data class Score(val taskName: String) {
    var score = 0L
    var day = 0
    private val projectOuts = mutableListOf<ProjectOut>()

    data class ProjectOut(val project: Project, val users: List<User>) {
    }

    fun addProject(project: Project, users: List<User>) {
        val projectEnd = day + project.days
        if (projectEnd <= project.bestBefore) {
            score += score + project.score
        } else {
            val scoreAdd = project.score - (projectEnd - project.bestBefore)
            if (scoreAdd > 0) {
                score += scoreAdd
            }
        }
        projectOuts.add(ProjectOut(project, users))
        day += project.days
    }

    fun print() {
        File("${taskName}.${score}.${System.currentTimeMillis()}.txt").printWriter().use { writer ->
            writer.println(projectOuts.size)
            projectOuts.forEach {
                writer.println(it.project.name)
                val userString = it.users.joinToString(separator = " ") { u -> u.name }
                writer.print(userString)
            }

        }
    }
}