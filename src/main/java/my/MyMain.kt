package my

object MyMain {

    @JvmStatic
    fun main(args: Array<String>) {
//        process(Task.a_an_example)
//        process(Task.b_better_start_small)
//        process(Task.c_collaboration)
//        process(Task.d_dense_schedule)
//        process(Task.e_exceptional_skills)
        process(Task.f_find_great_mentors)
    }
}

fun process(task: Task){
    println(task)
    val (users, projects) = parse(task)
    val result = greedy(users, projects)
    Score(task.name).print(result)
}

data class User(val name: String, val skills: MutableMap<String, Int>)

data class Project(val name: String, val days: Int, val score: Int, val bestBefore: Int, val roleToLevel: List<Pair<String, Int>>)

data class UsersAndProjects(val users: List<User>, val projects: List<Project>)

enum class Task{
    a_an_example,
    b_better_start_small,
    c_collaboration,
    d_dense_schedule,
    e_exceptional_skills,
    f_find_great_mentors,
}

fun parse(file: Task): UsersAndProjects{
     mutableListOf<User>()
    val projects = mutableListOf<Project>()

    MyMain::class.java.classLoader.getResourceAsStream("${file}.in.txt").bufferedReader().use {
        val (contributorsCount, projectsCount) = it.readLine().split(" ").map { it.toInt() }
        val users = (0 until contributorsCount).map{_ ->
            val contrLn = it.readLine()
            val name = contrLn.substringBeforeLast(" ")
            val skillCount = contrLn.substringAfterLast(" ").toInt()
            val skills = (0 until skillCount).map{_ ->
                val skillLn = it.readLine()
                val skillName = skillLn.substringBeforeLast(" ")
                val skillLevel = skillLn.substringAfterLast(" ").toInt()
                skillName to skillLevel
            }.toMap()
            User(name, skills.toMutableMap())
        }
        val projects = (0 until projectsCount).map {_ ->
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
