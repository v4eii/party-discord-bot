package ussr.party.kabachki.command.executor

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.EmbedCreateFields
import kotlinx.coroutines.delay
import ussr.party.kabachki.audio.PartyAudioManager
import ussr.party.kabachki.command.CommandExecutor
import ussr.party.kabachki.extension.*

class AudioExecutors {

    private val partyAudioManager = PartyAudioManager()

    val playExecutor = CommandExecutor { event, params ->
        if (params.checkValidUrl()) {
            event.getVoiceStateOrNull()?.let {
                println(1)
                partyAudioManager.runTrackOrAddToQueue(params)
                println(2)
                event.joinToMemberVoiceChannelWithProvider(partyAudioManager.provider)
                delay(2000)
                event.sendComplexMessageWithPlayedTrack()
            } ?: event.sendSimpleMessage(
                "Hey ${event.getUsername()}! You are not connected to the voice channel!"
            )
        } else {
            event.sendSimpleMessage("Please use next syntax: !play <url> [<urls>]")
        }
    }

    val skipExecutor = CommandExecutor { event, param ->
        partyAudioManager.skipTrack((param.firstOrNull()?.toIntOrNull() ?: 1) - 1).also {
            if (it) {
                event.sendSimpleMessage("Fine, next track")
                event.sendComplexMessageWithPlayedTrack()
            } else {
                event.sendSimpleMessage("Track queue is empty! Add new track with !play")
            }
        }
    }

    val pauseExecutor = CommandExecutor { event, _ ->
        partyAudioManager.stopTrack().also {
            if (it) {
                event.sendSimpleMessage("Track is paused")
            } else {
                event.sendSimpleMessage("No one track played")
            }
        }
    }

    val resumeExecutor = CommandExecutor { event, _ ->
        partyAudioManager.resumeTrack().also {
            if (it) {
                event.sendSimpleMessage("Track is resumed")
            } else {
                event.sendSimpleMessage("No one track played")
            }
        }
    }

    val showQueueExecutor = CommandExecutor { event, _ ->
        event.sendSimpleMessage(partyAudioManager.getTrackQueue().toIndexedStringList())
    }

    private suspend fun MessageCreateEvent.sendComplexMessageWithPlayedTrack() {
        val currentTrackInfo = partyAudioManager.getCurrentPlayedTrackInfo()
        val imageUrl =
            "https://upload.wikimedia.org/wikipedia/commons/thumb/3/35/Simple_Music.svg/1024px-Simple_Music.svg.png"
        sendComplexMessage(
            imageUrl = imageUrl,
            title = currentTrackInfo.title,
            description = "Author: ${currentTrackInfo.author}",
            attachmentTitleUrl = currentTrackInfo.uri,
            additionalFields = arrayOf(
                EmbedCreateFields.Field.of("length", currentTrackInfo.length.toHumanReadableLength(), true),
                EmbedCreateFields.Field.of("requestedBy", this.getMentionUsername(), true),
            )
        )
    }

    private fun Long.toHumanReadableLength(): String = run {
        listOf(this / 3600000, this / 60000 % 60, this / 1000 % 60)
            .joinToString(":") { it.toString().padStart(2, '0') }
            .dropWhile { it == '0' || it == ':' }
    }

    private fun List<AudioTrack>.toIndexedStringList() =
        withIndex().joinToString("\n") { "${it.index + 1}. ${it.value.info.title}" }
}