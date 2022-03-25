package ussr.party.kabachki.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import ussr.party.kabachki.audio.PartyAudioManager
import ussr.party.kabachki.command.executor.AudioCommands.sendComplexMessageWithPlayedTrack
import ussr.party.kabachki.extension.*

class PlayCommand(private val partyAudioManager: PartyAudioManager) : Command {
    override fun getName() = "play"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        event.getOptionByNameOrNull("url")?.let { trackUrl ->
            event.getVoiceStateOrNull()?.let {
                val urls = event.getOptionByNameOrNull("urls")
                    ?.split(" ")
                    ?.toTypedArray() ?: emptyArray()
                partyAudioManager.runTrackOrAddToQueue(listOf(trackUrl, *urls))
                event.joinToMemberVoiceChannelWithProvider(partyAudioManager.provider)
                event.replyTo(
                    content = "Wait a sec",
                    ephemeral = true
                )
                event.getMessageChannelOrThrow().sendComplexMessageWithPlayedTrack(event.getUserMention())
            } ?: event.replyTo("Hey ${event.getUsername()}! You are not connected to the voice channel!")
        } ?: event.replyTo("Please use next syntax: !play <url> [<urls>]")
    }
}