package ussr.party.kabachki.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TrackLoadHandlerImpl(private val scheduler: AudioTrackScheduler) : AudioLoadResultHandler {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun trackLoaded(track: AudioTrack) {
        logger.info("Track loaded! ${track.info.title}")
        scheduler.play(track)
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        playlist.tracks.forEach {
            logger.info("Track loaded! ${it.info.title}")
            scheduler.play(it)
        }
        logger.info("playlist loaded ${playlist.name}")
    }

    override fun noMatches() {
        logger.warn("track not found")
    }

    override fun loadFailed(exception: FriendlyException) {
        logger.error("error load track", exception)
    }
}