package ussr.party.kabachki.command.impl

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import ussr.party.kabachki.audio.PartyAudioManager
import ussr.party.kabachki.command.Command
import ussr.party.kabachki.extension.replyTo

class PauseCommand(private val partyAudioManager: PartyAudioManager) : Command {
    override fun getName() = "pause"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        partyAudioManager.stopTrack().also {
            event.run {
                if (it) {
                    replyTo("Track is paused")
                } else {
                    replyTo("No one track played", true)
                }
            }
        }
    }
}