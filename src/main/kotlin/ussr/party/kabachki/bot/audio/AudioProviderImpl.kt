package ussr.party.kabachki.bot.audio

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import discord4j.voice.AudioProvider
import java.nio.ByteBuffer

class AudioProviderImpl(private val player: AudioPlayer) :
    AudioProvider(ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize())) {

    private val frame: MutableAudioFrame = MutableAudioFrame().apply {
        setBuffer(buffer)
    }

    override fun provide(): Boolean {
        val isProvide = player.provide(frame)
        if (isProvide) {
            buffer.flip()
        }
        return isProvide
    }
}