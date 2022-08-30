package ussr.party.kabachki.command.impl

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import ussr.party.kabachki.audio.PartyAudioManager
import ussr.party.kabachki.command.Command
import ussr.party.kabachki.command.helper.AudioHelper.sendComplexMessageWithPlayedTrack
import ussr.party.kabachki.extension.getMessageChannelOrThrow
import ussr.party.kabachki.extension.getOptionByNameOrNull
import ussr.party.kabachki.extension.getUserMention
import ussr.party.kabachki.extension.replyTo

class SkipCommand(private val partyAudioManager: PartyAudioManager) : Command {
    override fun getName() = "skip"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        event.run {
            val position = getOptionByNameOrNull("position")?.asLong()?.toInt()
            partyAudioManager.skipTrack((position ?: 1) - 1).also {
                if (it) {
                    replyTo("Fine, next track")
                    getMessageChannelOrThrow().sendComplexMessageWithPlayedTrack(event.getUserMention())
                } else {
                    replyTo("Track queue is empty! Add new track with !play")
                }
            }
        }
    }
}