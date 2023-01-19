package ussr.party.kabachki.bot.command.impl

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import kotlinx.coroutines.delay
import org.springframework.stereotype.Component
import ussr.party.kabachki.bot.command.Command
import ussr.party.kabachki.bot.extension.replyTo

@Component
class PingCommand : Command {
    override fun getName() = "ping"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        delay(2000)
        event.replyTo("Pong!", true)
    }
}
