import Replay.downloadReplay
import Replay.listLastBattles
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.cookies.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import java.io.File

val ymlMapper = ObjectMapper(YAMLFactory().apply {
    this.enable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE)
}).apply {
    this.registerModule(KotlinModule())
    this.writerWithDefaultPrettyPrinter()
}

object Replay {
    val userId = 3871137

    val httpClient: HttpClient by lazy {
        HttpClient(Apache) {
            install(HttpCookies) {
                storage = ConstantCookiesStorage(
                    Cookie(
                        "cgSession",
                        "ee815596-7143-47b2-9313-cd879439a391",
                        domain = "www.codingame.com"
                    )
                )
            }

            install(JsonFeature) {
                serializer = JacksonSerializer()
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val replayId = "627384758"

        val replayFile = File("replays/$replayId.txt")

        val input = if (replayFile.exists()) {
            replayFile.readText()
        } else {
            val replayText = downloadReplay(replayId)
            replayText
        }

        try {
            System.setIn(input.byteInputStream())
            logStream = System.out
            performGame(echo = false)
        } catch (t: Throwable) {
            System.err.println(t)
        }

    }

    fun downloadReplay(replayId: String): String {
        return runBlocking {
            //get replay
            val gameInfo =
                httpClient.post<ReplayInfo>("https://www.codingame.com/services/gameResult/findInformationById") {
                    body = listOf<Any>(replayId, userId)
                    contentType(ContentType.Application.Json)
                }.gameResult

            val names = gameInfo.agents.map { it.index to it.codingamer.pseudo }.toMap()

            val rendered = gameInfo.copy(frames = gameInfo.frames.map {
                it.copy(
                    summary = it.summary?.replace("\$0", names[0]!!)?.replace("\$1", names[1]!!)
                )
            })

            val stdError = gameInfoToInput(gameInfo)
            File("replays/$replayId.raw.txt").apply {
                parentFile.mkdirs()
                writeText(stdError)
            }
            ymlMapper.writeValue(File("replays/$replayId.yml"), rendered)
            val filtered = stdError.lineSequence().filterNot { it.startsWith("#") }.joinToString(separator = "\n")
            File("replays/$replayId.txt").writeText(filtered)
            return@runBlocking filtered
        }
    }

    fun listLastBattles(sessionHandle: String): List<String> {
        return runBlocking {
            //get replay
            val gameInfo =
                httpClient.post<List<GameInfo>>("https://www.codingame.com/services/gamesPlayersRanking/findLastBattlesByTestSessionHandle") {
                    body = listOf(sessionHandle, null)
                    contentType(ContentType.Application.Json)
                }

            gameInfo.map { it.gameId }
        }
    }
}

private fun gameInfoToInput(gameInfo: Game): String {
    return gameInfo.frames
        .mapNotNull { it.stderr }
        .joinToString("\n")
        .lines()
        .joinToString("\n")
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class Frame(
    val gameInformation: String? = null,
    val stdout: String? = null,
    val stderr: String? = null,
    val summary: String? = null,
//    val view: String? = null,
    val keyframe: String? = null,
    val agentId: String? = null
) {

}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ReplayInfo(
    val gameResult: Game
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Game(
    val frames: List<Frame>,
    val agents: List<Agent>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Agent(
    val index: Int,
    val codingamer: Codingamer
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Codingamer(
    val pseudo: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GameInfo(
    val gameId: String
)

object Transformer {
    @JvmStatic
    fun main(args: Array<String>) {
        val name = "1"
        val from = File("replays/$name.json")
        val to = File("replays/$name.txt")

        to.writeText(
            gameInfoToInput(
                jacksonObjectMapper().readValue<Game>(from)
            )
        )
    }
}

object DownloadLast {
    @JvmStatic
    fun main(args: Array<String>) {
        val games = listLastBattles("295709813627112ed42900bdc3f5ff26792c2c1b")
        games.forEach {
            downloadReplay(it)
        }
    }
}



