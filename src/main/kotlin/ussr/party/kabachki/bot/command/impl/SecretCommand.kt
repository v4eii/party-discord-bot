package ussr.party.kabachki.bot.command.impl

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.springframework.stereotype.Component
import ussr.party.kabachki.bot.command.Command
import ussr.party.kabachki.bot.extension.getUsername
import ussr.party.kabachki.bot.extension.isDeveloper
import ussr.party.kabachki.bot.extension.replyTo

@Component
class SecretCommand : Command {
    override fun getName() = "secret"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        event.run {
            if (isDeveloper()) {
//                replyTo("secret token: ${configHolder["token"]}", true)
            } else {
                replyTo("Hey ${event.getUsername()}, you are not developer")
            }
        }
    }
}