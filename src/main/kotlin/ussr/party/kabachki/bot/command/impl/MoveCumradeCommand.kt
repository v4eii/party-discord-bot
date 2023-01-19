package ussr.party.kabachki.bot.command.impl

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.stereotype.Component
import ussr.party.kabachki.bot.command.Command
import ussr.party.kabachki.bot.extension.*

@Component
class MoveCumradeCommand : Command {
    override fun getName() = "move-cumrade"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        event.run {
            if (isDeveloper()) {
                val destination = getOptionByNameOrThrow("destination").asChannel().awaitFirst()
                val voiceChannel = getMemberOrThrow().getVoiceStateOrThrow().getVoiceChannelOrThrow()
                val members = voiceChannel.members.collectList().awaitFirst()
                members.filter {
                    voiceChannel.isMemberConnected(it.id).awaitFirst()
                }.forEach {
                    it.edit().withNewVoiceChannel(destination.id.toOptional().toPossible()).subscribe()
                }
                replyTo("Cumrades! Welcume to ${destination.mention}")
            } else {
                replyTo("You are not Developer!", true)
            }
        }
    }
}