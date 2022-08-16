package ussr.party.kabachki.command.impl

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import ussr.party.kabachki.command.Command
import ussr.party.kabachki.configHolder
import ussr.party.kabachki.extension.getUsername
import ussr.party.kabachki.extension.isDeveloper
import ussr.party.kabachki.extension.replyTo

class SecretCommand : Command {
    override fun getName() = "secret"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        event.run {
            if (isDeveloper()) {
                replyTo("secret token: ${configHolder["token"]}", true)
            } else {
                replyTo("Hey ${event.getUsername()}, you are not developer")
            }
        }
    }
}