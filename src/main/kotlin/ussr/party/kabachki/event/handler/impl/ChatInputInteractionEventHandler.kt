package ussr.party.kabachki.event.handler.impl

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import ussr.party.kabachki.command.Command
import ussr.party.kabachki.command.helper.AudioHelper
import ussr.party.kabachki.command.impl.MoveCumradeCommand
import ussr.party.kabachki.command.impl.PingCommand
import ussr.party.kabachki.command.impl.SecretCommand
import ussr.party.kabachki.command.impl.WeatherCommand
import ussr.party.kabachki.event.handler.EventHandler

class ChatInputInteractionEventHandler : EventHandler<ChatInputInteractionEvent> {

    private val commandList: List<Command> =
        listOf(
            PingCommand(),
            SecretCommand(),
            MoveCumradeCommand(),
            WeatherCommand(),
            AudioHelper.playCommand,
            AudioHelper.skipCommand,
            AudioHelper.pauseCommand,
            AudioHelper.resumeCommand,
            AudioHelper.listCommand,
        )

    override suspend fun handle(event: ChatInputInteractionEvent) {
        commandList.first { it.getName() == event.commandName }.executeCommand(event)
    }
}