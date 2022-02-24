package my

import my.Score.ProjectOut
import java.lang.Integer.max

data class Schedule(val day: Int, val users: List<User>, val projects: List<Project>)

fun greedy(users: List<User>, projects: List<Project>): List<ProjectOut> {

    val projects = projects.toMutableList()
    val projectToEndDay = mutableMapOf<Project, Int>()

    val result = mutableListOf<ProjectOut>()

    val skillToUser: Map<String, List<User>> =
        users.flatMap { user -> user.skills.keys.map { it to user } }.groupBy { it.first }
            .mapValues { it.value.map { it.second } }


    var day = 0

    val usersToFreeDay = users.associateWith { 0 }.toMutableMap()

    while (true) {


        fun Project.realScore() = if (bestBefore > days + day) {
            score
        } else {
            max(0, score - (day + days + 1 - bestBefore))
        }

        fun Project.canTake(): Boolean {
            return this.roleToLevel.all { (role, level) ->
                val eligibleUsers = skillToUser[role] ?: return false
                eligibleUsers.any { usersToFreeDay[it]!! >= day && it.skills[role]!! >= level}
            }
        }

        val best = projects.filter { it.canTake() }.maxBy {
            it.realScore() * 1.0 / it.days / it.roleToLevel.size
        }

        fun Project.take(): Score.ProjectOut {
            projects.remove(this)
            val users = this.roleToLevel.map { (role, level) ->
                val user = skillToUser[role]!!.first { usersToFreeDay[it]!! >= day && it.skills[role]!! >= level }
                usersToFreeDay[user] = day + this.days
                user
            }
            projectToEndDay[this] = day + this.days
            return ProjectOut(this, users)
        }

        if (best == null){
            val nextFinish = projectToEndDay.filter { it.value > day }.values.min()
            if (nextFinish == null){
                break
            }
            day = nextFinish + 1
        }else {
            result.add(best.take())
        }
    }
    return result
}

