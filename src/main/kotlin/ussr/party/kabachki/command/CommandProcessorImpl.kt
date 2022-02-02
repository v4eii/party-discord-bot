package ussr.party.kabachki.command

import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ussr.party.kabachki.command.executor.AudioExecutors
import ussr.party.kabachki.command.executor.UtilExecutors
import ussr.party.kabachki.extension.getContent
import ussr.party.kabachki.extension.getUsername
import ussr.party.kabachki.extension.sendSimpleMessage

class CommandProcessorImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : CommandProcessor {

    private val audioExecutors: AudioExecutors = AudioExecutors()
    private val utilExecutors: UtilExecutors = UtilExecutors()

    private val commandExecutorsPairList: List<Pair<String, CommandExecutor<*>>> =
        listOf(
            "!ping" to CommandExecutor { event, _ -> event.sendSimpleMessage("Pong!") },
            "!secret" to utilExecutors.secretExecutor,
            "!play" to audioExecutors.playExecutor,
            "!skip" to audioExecutors.skipExecutor,
            "!pause" to audioExecutors.pauseExecutor,
            "!resume" to audioExecutors.resumeExecutor,
            "!queue" to audioExecutors.showQueueExecutor,
            "!doSomething" to utilExecutors.doSomethingExecutor
        )

    override suspend fun parseCommand(event: MessageCreateEvent) = withContext(dispatcher) {
        val content = event.getContent().trim()
        if (content.isCommand()) {
            val splitCommand = content.split(" ").map { it.trim() }

            commandExecutorsPairList.find { it.first == splitCommand[0] }
                ?.second
                ?.executeCommand(event, splitCommand.subList(1, splitCommand.size))
                ?: event.sendSimpleMessage("Sorry ${event.getUsername()}, i don't known this command")
        }

    }

    override fun String.isCommand() = startsWith("!")
}