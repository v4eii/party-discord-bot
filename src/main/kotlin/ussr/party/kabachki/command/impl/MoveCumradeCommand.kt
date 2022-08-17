package ussr.party.kabachki.command.impl

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import kotlinx.coroutines.reactive.awaitFirst
import ussr.party.kabachki.command.Command
import ussr.party.kabachki.extension.*

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
                    it.edit().withNewVoiceChannel(destination.id.toOptional().toPossible()).subscribe().also { println(321) }
                }
                replyTo("Cumrades! Welcume to ${destination.mention}")
            } else {
                replyTo("You are not Developer!", true)
            }
        }
    }
}