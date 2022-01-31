package ussr.party.kabachki.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import java.util.*

class AudioTrackScheduler(
    private val player: AudioPlayer
) : AudioEventAdapter() {

    val queue: MutableList<AudioTrack> = Collections.synchronizedList(LinkedList())

    @JvmOverloads
    fun play(track: AudioTrack, force: Boolean = false) =
        player.startTrack(track, force.not()).also {
            if (!it) {
                queue.add(track)
            } else {
                println("played ${player.playingTrack.info.title}")
            }
        }

    private fun nextForce(): Boolean = queue.isEmpty().not() && play(queue.removeAt(0), true)

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if (endReason.mayStartNext) nextForce()
    }
}