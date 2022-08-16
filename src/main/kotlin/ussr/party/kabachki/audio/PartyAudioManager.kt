package ussr.party.kabachki.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrameBufferFactory
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer
import discord4j.voice.AudioProvider

class PartyAudioManager {

    private val playerManager: AudioPlayerManager = DefaultAudioPlayerManager().apply {
        configuration.frameBufferFactory = AudioFrameBufferFactory { i, audioDataFormat, atomicBoolean ->
            NonAllocatingAudioFrameBuffer(i, audioDataFormat, atomicBoolean)
        }
        AudioSourceManagers.registerRemoteSources(this)
    }
    private val player: AudioPlayer = playerManager.createPlayer()
    private val scheduler: AudioTrackScheduler = AudioTrackScheduler(player)
    private val handler: TrackLoadHandlerImpl = TrackLoadHandlerImpl(scheduler)
    val provider: AudioProvider = AudioProviderImpl(player)

    fun runTrackOrAddToQueue(resources: List<String>) {
        resources.forEach {
            playerManager.loadItem(it, handler).get()
        }
    }

    fun resumeTrack(): Boolean =
        if (player.playingTrack == null) {
            false
        } else if (player.isPaused) {
            revertPauseFlag()
            true
        } else {
            true
        }

    fun stopTrack(): Boolean =
        if (player.playingTrack == null) {
            false
        } else if (player.isPaused.not()) {
            revertPauseFlag()
            true
        } else {
            true
        }


    private fun revertPauseFlag() {
        player.isPaused = !player.isPaused
    }

    fun getCurrentPlayedTrackInfo(): AudioTrackInfo = player.playingTrack.info
    fun skipTrack(index: Int = 0) = scheduler.nextForce(index)
    fun getTrackQueue() = scheduler.queue
}