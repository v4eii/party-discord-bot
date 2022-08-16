package ussr.party.kabachki.command.processor

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ussr.party.kabachki.command.Command
import ussr.party.kabachki.command.executor.AudioCommands
import ussr.party.kabachki.command.impl.MoveCumradeCommand
import ussr.party.kabachki.command.impl.PingCommand
import ussr.party.kabachki.command.impl.SecretCommand

class CommandProcessorImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : CommandProcessor {
    private val commandList: List<Command> =
        listOf(
            PingCommand(),
            SecretCommand(),
            MoveCumradeCommand(),
            AudioCommands.playCommand,
            AudioCommands.skipCommand,
            AudioCommands.pauseCommand,
            AudioCommands.resumeCommand,
            AudioCommands.listCommand,
        )

    override suspend fun handle(event: ChatInputInteractionEvent) = withContext(dispatcher) {
        commandList.first { it.getName() == event.commandName }.executeCommand(event)
    }
}