package ussr.party.kabachki.command.processor

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ussr.party.kabachki.command.Command
import ussr.party.kabachki.command.PingCommand
import ussr.party.kabachki.command.SecretCommand
import ussr.party.kabachki.command.executor.AudioCommands

class CommandProcessorImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : CommandProcessor {
    private val commandList: List<Command> =
        listOf(
            PingCommand(),
            SecretCommand(),
            AudioCommands.playCommand,
            AudioCommands.skipCommand,
            AudioCommands.pauseCommand,
            AudioCommands.resumeCommand,
            AudioCommands.listCommand
        )

    override suspend fun handle(event: ChatInputInteractionEvent) = withContext(dispatcher) {
        commandList.first { it.getName() == event.commandName }.executeCommand(event)
        delay(1000)
    }
}