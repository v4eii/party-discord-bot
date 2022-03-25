package ussr.party.kabachki.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import ussr.party.kabachki.audio.PartyAudioManager
import ussr.party.kabachki.extension.replyTo

class ResumeCommand(private val partyAudioManager: PartyAudioManager) : Command {
    override fun getName() = "resume"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        partyAudioManager.resumeTrack().also {
            if (it) {
                event.replyTo("Track is resumed")
            } else {
                event.replyTo("No one track played", true)
            }
        }
    }
}