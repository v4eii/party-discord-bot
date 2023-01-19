package ussr.party.kabachki.bot.command.impl

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.springframework.stereotype.Component
import ussr.party.kabachki.bot.audio.PartyAudioManager
import ussr.party.kabachki.bot.command.Command
import ussr.party.kabachki.bot.extension.*

@Component
class PlayCommand(private val partyAudioManager: PartyAudioManager) : Command {
    override fun getName() = "play"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        event.run {
            getOptionByNameOrNull("url")?.asString()?.let { trackUrl ->
                getVoiceStateOrNull() ?: replyTo("Hey ${getUserMention()}! You are not connected to the voice channel!")
                val urls = getOptionByNameOrNull("urls")?.asString()
                    ?.split(" ")
                    ?.toTypedArray() ?: emptyArray()
                partyAudioManager.runTrackOrAddToQueue(listOf(trackUrl, *urls))
                joinToMemberVoiceChannelWithProvider(partyAudioManager.provider)
                replyTo(
                    content = "Wait a sec",
                    ephemeral = true
                )
                getMessageChannelOrThrow().sendComplexMessageWithPlayedTrack(getUserMention(), partyAudioManager.getCurrentPlayedTrackInfo())
//
            } ?: replyTo("Please use next syntax: !play <url> [<urls>]")
        }
    }
}