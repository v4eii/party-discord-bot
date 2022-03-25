package ussr.party.kabachki.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import ussr.party.kabachki.audio.PartyAudioManager
import ussr.party.kabachki.command.executor.AudioCommands.sendComplexMessageWithPlayedTrack
import ussr.party.kabachki.extension.getMessageChannelOrThrow
import ussr.party.kabachki.extension.getOptionByNameOrNull
import ussr.party.kabachki.extension.getUserMention
import ussr.party.kabachki.extension.replyTo

class SkipCommand(private val partyAudioManager: PartyAudioManager) : Command {
    override fun getName() = "skip"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        val position = event.getOptionByNameOrNull("position")?.toInt()
        partyAudioManager.skipTrack((position ?: 1) - 1).also {
            if (it) {
                event.replyTo("Fine, next track")
                event.getMessageChannelOrThrow().sendComplexMessageWithPlayedTrack(event.getUserMention())
            } else {
                event.replyTo("Track queue is empty! Add new track with !play")
            }
        }
    }
}