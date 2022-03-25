package ussr.party.kabachki.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import ussr.party.kabachki.audio.PartyAudioManager
import ussr.party.kabachki.command.executor.AudioCommands
import ussr.party.kabachki.extension.replyTo

class ListCommand(private val partyAudioManager: PartyAudioManager) : Command {
    override fun getName() = "list"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        with(AudioCommands) {
            val list = partyAudioManager.getTrackQueue()
            event.replyTo(if (list.isEmpty()) "List is empty" else list.toIndexedStringList())
        }
    }
}