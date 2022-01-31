package ussr.party.kabachki.extension

import discord4j.core.`object`.entity.Member
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.VoiceChannelJoinSpec
import discord4j.voice.AudioProvider
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull

fun MessageCreateEvent.getMemberOrThrow(): Member = this.member.orElseThrow { RuntimeException() } // TODO: add custom exception

suspend fun MessageCreateEvent.joinToMemberVoiceChannelWithProvider(audioProvider: AudioProvider) { // TODO: i think this should be decompose
    val member = getMemberOrThrow()
    member.voiceState
        .awaitSingleOrNull()
        ?.channel
        ?.flatMap { channel ->
            channel.join(
                VoiceChannelJoinSpec.builder()
                    .provider(audioProvider)
                    .build()
            )
        }?.awaitSingleOrNull()
        ?: sendSimpleMessage("Hey ${member.userData.username()}! You are not connected to the voice channel! ")
}

suspend fun MessageCreateEvent.sendSimpleMessage(msg: String) { // TODO: i think this should be decompose
    this.message.channel
        .awaitSingle()
        .createMessage(msg)
        .awaitSingle()
}