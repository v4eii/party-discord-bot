package ussr.party.kabachki.bot.command.impl

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.springframework.stereotype.Component
import ussr.party.kabachki.bot.audio.PartyAudioManager
import ussr.party.kabachki.bot.command.Command
import ussr.party.kabachki.bot.extension.replyTo

@Component
class ListCommand(private val partyAudioManager: PartyAudioManager) : Command {
    override fun getName() = "list"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        val list = partyAudioManager.getTrackQueue()
        event.replyTo(if (list.isEmpty()) "List is empty" else list.toIndexedStringList())
    }

    private fun List<AudioTrack>.toIndexedStringList() =
        withIndex().joinToString("\n") { "${it.index + 1}. ${it.value.info.title}" }
}