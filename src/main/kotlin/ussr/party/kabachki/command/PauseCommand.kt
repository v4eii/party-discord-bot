package ussr.party.kabachki.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import ussr.party.kabachki.audio.PartyAudioManager
import ussr.party.kabachki.extension.replyTo

class PauseCommand(private val partyAudioManager: PartyAudioManager) : Command {
    override fun getName() = "pause"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        partyAudioManager.stopTrack().also {
            if (it) {
                event.replyTo("Track is paused")
            } else {
                event.replyTo("No one track played", true)
            }
        }
    }
}