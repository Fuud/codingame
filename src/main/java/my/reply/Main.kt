package my.reply

import java.util.*

fun main() {
    listOf(
    "00-example.txt",
    "01-the-cloud-abyss.txt",
    "02-iot-island-of-terror.txt",
    "03-etheryum.txt",
    "04-the-desert-of-autonomous-machines.txt",
    "05-androids-armageddon.txt",
    ).forEach { file ->
        println(file)
        val game = parse(file)
        println(game)
        solve(game)
    }
}

fun parse(file: String): Game{
    return Scanner(Game::class.java.classLoader.getResourceAsStream("reply/$file")).use { scanner ->
        val initStamina = scanner.nextInt()
        val maxStamina = scanner.nextInt()
        val maxTurns = scanner.nextInt()
        val daemonCount = scanner.nextInt()

        val hero = Hero(initStamina, maxStamina)

        val daemons = (0 until daemonCount).map {
            Daemon(
                id = it,
                stamina = scanner.nextInt(),
                turnToRecover = scanner.nextInt(),
                staminaRecover = scanner.nextInt(),
                prises = scanner.nextInt().let { pricesCount ->
                    (0 until pricesCount).map { scanner.nextInt() }
                }
            )
        }

        Game(maxTurns, hero, daemons)
    }
}

