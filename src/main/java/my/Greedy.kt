package my

import my.Score.ProjectOut
import java.lang.Integer.max
import java.util.Random

fun greedy(users: List<User>, projects: List<Project>): List<ProjectOut> {

    val projects = projects.toMutableList()
    val projectToEndDay = mutableMapOf<Project, Int>()

    val result = mutableListOf<ProjectOut>()

    val skillToUser: Map<String, List<User>> =
        users.flatMap { user -> user.skills.keys.map { it to user } }.groupBy { it.first }
            .mapValues { it.value.map { it.second } }


    var day = 0

    val usersToFreeDay = users.associateWith { 0 }.toMutableMap()

    val random = Random()
    while (true) {
        fun Project.realScore() = (if (bestBefore > days + day) {
            score
        } else {
            max(0, score - (day + days + 1 - bestBefore))
        }) * (0.8 + random.nextDouble() * 0.2)

        fun Project.canTake(): Boolean {
            return this.roleToLevel.all { (role, level) ->
                val eligibleUsers = skillToUser[role] ?: return false
                eligibleUsers.any { usersToFreeDay[it]!! <= day && it.skills[role]!! >= level }
            }
        }

        fun Project.take(): ProjectOut? {
            val usersToFreeDayCopy = mutableMapOf<User, Int>()
            val mentors = mutableMapOf<String, Int>()
            val userToLevel = this.roleToLevel.map { (role, level) ->
                val hasMentor = (mentors[role] ?: -1) >= level
                val requiredLevel = if (hasMentor) {
                    level - 1
                } else {
                    level
                }
                val user = skillToUser[role]!!.filter {
                    (usersToFreeDayCopy[it] ?: usersToFreeDay[it]!!) <= day && it.skills[role]!! >= requiredLevel
                }.minBy { it.skills[role]!! } ?: return null
                user.skills.forEach { (role, level) ->
                    if ((mentors[role] ?: -1) < level) {
                        mentors[role] = level
                    }
                }
                usersToFreeDayCopy[user] = day + this.days
                val up = user.skills[role]!! <= level
                user to (up to role)
            }
            userToLevel.forEach { (user, pair) ->
                val (up, role) = pair
                if (up) {
                    user.skills[role] = user.skills[role]!! + 1
                }
            }
            usersToFreeDay.putAll(usersToFreeDayCopy)
            projectToEndDay[this] = day + this.days
            projects.remove(this)
            return ProjectOut(this, userToLevel.map { it.first })
        }

        val sorted = projects.filter { it.canTake() }.map { it to it.realScore() }.sortedBy { (pr, score) ->
            score * 1.0 / pr.days / pr.roleToLevel.size
        }
        val best = sorted
            .asSequence().map { it.first.take() }.firstOrNull { it != null }

        if (best == null) {
            val nextFinish = projectToEndDay.filter { it.value > day }.values.min() ?: break
            day = nextFinish + 1
        } else {
            result.add(best)
        }
    }
    return result
}

