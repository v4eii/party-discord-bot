package ussr.party.kabachki.command.executor

import ussr.party.kabachki.audio.PartyAudioManager
import ussr.party.kabachki.command.CommandExecutor
import ussr.party.kabachki.extension.*

class AudioExecutors {
    private val partyAudioManager = PartyAudioManager()

    val playExecutor = CommandExecutor { event, params ->
        if (params.checkValidUrl()) {
            event.getVoiceStateOrNull()?.let {
                partyAudioManager.runTrackOrAddToQueue(params)
                event.joinToMemberVoiceChannelWithProvider(partyAudioManager.provider)
                event.sendSimpleMessage(partyAudioManager.getCurrentPlayedTrack())
            } ?: event.sendSimpleMessage(
                "Hey ${event.getUsername()}! You are not connected to the voice channel!"
            )
        } else {
            event.sendSimpleMessage("Please use next syntax: !play <url> [<urls>]")
        }
    }
    val skipExecutor = CommandExecutor { event, _ ->
        partyAudioManager.skipCurrentTrack().also {
            if (it) {
                event.sendSimpleMessage("Fine, next track: ${partyAudioManager.getCurrentPlayedTrack()}") // change to complex msg
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
}