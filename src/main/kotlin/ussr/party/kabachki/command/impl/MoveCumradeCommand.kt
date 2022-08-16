package ussr.party.kabachki.command.impl

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import ussr.party.kabachki.command.Command
import ussr.party.kabachki.extension.*

class MoveCumradeCommand : Command {
    override fun getName() = "move-cumrade"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        event.run {
            if (isDeveloper()) {
                val destination = getOptionByNameOrThrow("destination").asChannel().awaitFirst()
                getMemberOrThrow().getVoiceStateOrThrow()
                    .getVoiceChannelOrThrow()
                    .members
                    .asFlow()
                    .collect {
                        it.edit().withNewVoiceChannel(destination.id.toOptional().toPossible()).awaitFirstOrNull()
                    }
            } else {
                replyTo("You are not Developer!", true)
            }
        }
    }
}