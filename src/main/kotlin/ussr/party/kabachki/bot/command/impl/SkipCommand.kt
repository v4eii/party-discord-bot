package ussr.party.kabachki.bot.command.impl

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.springframework.stereotype.Component
import ussr.party.kabachki.bot.audio.PartyAudioManager
import ussr.party.kabachki.bot.command.Command
import ussr.party.kabachki.bot.extension.*

@Component
class SkipCommand(private val partyAudioManager: PartyAudioManager) : Command {
    override fun getName() = "skip"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        event.run {
            val position = getOptionByNameOrNull("position")?.asLong()?.toInt()
            partyAudioManager.skipTrack((position ?: 1) - 1).also {
                if (it) {
                    replyTo("Fine, next track")
                    getMessageChannelOrThrow().sendComplexMessageWithPlayedTrack(
                        event.getUserMention(),
                        partyAudioManager.getCurrentPlayedTrackInfo()
                    )
                } else {
                    replyTo("Track queue is empty! Add new track with !play")
                }
            }
        }
    }
}