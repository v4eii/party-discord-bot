package ussr.party.kabachki.command.impl

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import kotlinx.coroutines.delay
import ussr.party.kabachki.command.Command
import ussr.party.kabachki.extension.replyTo

class PingCommand : Command {
    override fun getName() = "ping"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        delay(2000)
        event.replyTo("Pong!", true)
    }
}
