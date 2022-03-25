package ussr.party.kabachki.command.executor

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.spec.EmbedCreateFields
import ussr.party.kabachki.audio.PartyAudioManager
import ussr.party.kabachki.command.*
import ussr.party.kabachki.extension.sendComplexMessage

object AudioCommands {
    private val partyAudioManager = PartyAudioManager()

    val playCommand = PlayCommand(partyAudioManager)
    val skipCommand = SkipCommand(partyAudioManager)
    val pauseCommand = PauseCommand(partyAudioManager)
    val resumeCommand = ResumeCommand(partyAudioManager)
    val listCommand = ListCommand(partyAudioManager)

    suspend fun MessageChannel.sendComplexMessageWithPlayedTrack(mention: String) {
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
                EmbedCreateFields.Field.of("requestedBy", mention, true),
            )
        )
    }

    fun List<AudioTrack>.toIndexedStringList() =
        withIndex().joinToString("\n") { "${it.index + 1}. ${it.value.info.title}" }

    private fun Long.toHumanReadableLength(): String = run {
        listOf(this / 3600000, this / 60000 % 60, this / 1000 % 60)
            .joinToString(":") { it.toString().padStart(2, '0') }
            .dropWhile { it == '0' || it == ':' }
    }
}