package ussr.party.kabachki.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import ussr.party.kabachki.extension.replyTo

class PingCommand : Command {
    override fun getName() = "ping"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        event.replyTo("Pong!", true)
    }
}
