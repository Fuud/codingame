package my.reply

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
        solve(game)
    }
}

fun parse(file: String): Triple<Hero, Game, List<Daemon>>{
    TODO()
}

