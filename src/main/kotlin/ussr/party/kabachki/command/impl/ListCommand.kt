package ussr.party.kabachki.command.impl

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import ussr.party.kabachki.audio.PartyAudioManager
import ussr.party.kabachki.command.Command
import ussr.party.kabachki.extension.replyTo

class ListCommand(private val partyAudioManager: PartyAudioManager) : Command {
    override fun getName() = "list"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        val list = partyAudioManager.getTrackQueue()
        event.replyTo(if (list.isEmpty()) "List is empty" else list.toIndexedStringList())
    }

    private fun List<AudioTrack>.toIndexedStringList() =
        withIndex().joinToString("\n") { "${it.index + 1}. ${it.value.info.title}" }
}