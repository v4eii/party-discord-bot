package ussr.party.kabachki.command.impl

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import ussr.party.kabachki.audio.PartyAudioManager
import ussr.party.kabachki.command.Command
import ussr.party.kabachki.command.helper.AudioHelper.sendComplexMessageWithPlayedTrack
import ussr.party.kabachki.extension.*

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
                getMessageChannelOrThrow().sendComplexMessageWithPlayedTrack(getUserMention())
//
            } ?: replyTo("Please use next syntax: !play <url> [<urls>]")
        }
    }
}