package my

object MyMain {

    @JvmStatic
    fun main(args: Array<String>) {
        val tasks = listOf(
            Task.b,
            Task.c,
            Task.d,
            Task.e,
            Task.f,
        )
        val task2description = tasks.associateWith(::parse)
        while (true) {
            task2description.forEach { (task, description) ->
                val result = greedy(description)
                println(task.toString()+  "" + Score(task).print(result))
            }
        }
    }
}

data class User(val name: String, val skills: Map<String, Int>) {
    fun mutate() = MutableUser(name, skills.toMutableMap())
}

class MutableUser(val name: String, val skills: MutableMap<String, Int>)

data class Project(val name: String, val days: Int, val score: Int, val bestBefore: Int, val roleToLevel: List<Pair<String, Int>>)

data class UsersAndProjects(val users: List<User>, val projects: List<Project>)

enum class Task(val description: String) {
    a("an_example"),
    b("better_start_small"),
    c("collaboration"),
    d("dense_schedule"),
    e("exceptional_skills"),
    f("find_great_mentors"),
}

fun parse(task: Task): UsersAndProjects {
    mutableListOf<User>()

    MyMain::class.java.classLoader.getResourceAsStream("${task}_${task.description}.in.txt").bufferedReader().use {
        val (contributorsCount, projectsCount) = it.readLine().split(" ").map { it.toInt() }
        val users = (0 until contributorsCount).map { _ ->
            val contrLn = it.readLine()
            val name = contrLn.substringBeforeLast(" ")
            val skillCount = contrLn.substringAfterLast(" ").toInt()
            val skills = (0 until skillCount).map { _ ->
                val skillLn = it.readLine()
                val skillName = skillLn.substringBeforeLast(" ")
                val skillLevel = skillLn.substringAfterLast(" ").toInt()
                skillName to skillLevel
            }.toMap()
            User(name, skills.toMutableMap())
        }
        val projects = (0 until projectsCount).map { _ ->
            val projectLn = it.readLine()
            val projectParts = projectLn.split(" ")
            val name = projectParts.dropLast(4).joinToString(separator = " ")
            val (days, score, bestBefore, rolesCount) = projectParts.takeLast(4).map { it.toInt() }
            val roleToLevel = (0 until rolesCount).map { _ ->
                val skillLn = it.readLine()
                val skillName = skillLn.substringBeforeLast(" ")
                val skillLevel = skillLn.substringAfterLast(" ").toInt()
                skillName to skillLevel
            }
            Project(name, days, score, bestBefore, roleToLevel)
        }
        return UsersAndProjects(users, projects)
    }
}
