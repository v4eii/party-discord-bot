package ussr.party.kabachki.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import org.slf4j.LoggerFactory
import java.util.*

class AudioTrackScheduler(
    private val player: AudioPlayer
) : AudioEventAdapter() {

    val queue: MutableList<AudioTrack> = Collections.synchronizedList(LinkedList())

    private val logger = LoggerFactory.getLogger(this::class.java)

    @JvmOverloads
    fun play(track: AudioTrack, force: Boolean = false) =
        player.startTrack(track, force.not()).also {
            if (!it) {
                queue.add(track)
            } else {
                logger.info("now played ${player.playingTrack.info.title}")
            }
        }

    fun nextForce(index: Int = 0): Boolean = (queue.isEmpty().not() && play(queue.removeAt(index.safeIndex()), true))

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if (endReason.mayStartNext) nextForce()
    }

    private fun Int.safeIndex(): Int =
        if (this < 0) {
            0
        } else if (this > queue.size - 1) {
            queue.size - 1
        } else {
            this
        }
}