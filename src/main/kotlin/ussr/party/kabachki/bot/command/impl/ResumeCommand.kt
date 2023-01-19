package ussr.party.kabachki.bot.command.impl

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.springframework.stereotype.Component
import ussr.party.kabachki.bot.audio.PartyAudioManager
import ussr.party.kabachki.bot.command.Command
import ussr.party.kabachki.bot.extension.replyTo

@Component
class ResumeCommand(private val partyAudioManager: PartyAudioManager) : Command {
    override fun getName() = "resume"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        partyAudioManager.resumeTrack().also {
            event.run {
                if (it) {
                    replyTo("Track is resumed")
                } else {
                    replyTo("No one track played", true)
                }
            }
        }
    }
}