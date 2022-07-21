package ussr.party.kabachki.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import kotlinx.coroutines.reactive.awaitFirstOrNull
import ussr.party.kabachki.configHolder
import ussr.party.kabachki.extension.getMemberOrThrow
import ussr.party.kabachki.extension.getUsername
import ussr.party.kabachki.extension.replyTo

class SecretCommand : Command {
    override fun getName() = "secret"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        suspend fun isDeveloper() =
            event.getMemberOrThrow()
                .roles
                .any { it.name == "Developer" }
                .awaitFirstOrNull() ?: false

        if (isDeveloper()) {
            event.replyTo("secret token: ${configHolder["token"]}", true)
        } else {
            event.replyTo("Hey ${event.getUsername()}, you are not developer")
        }
    }
}