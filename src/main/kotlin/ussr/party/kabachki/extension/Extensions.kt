package ussr.party.kabachki.extension

import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.EmbedCreateFields
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.VoiceChannelJoinSpec
import discord4j.voice.AudioProvider
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import ussr.party.kabachki.exception.MemberIsNotPresentException
import ussr.party.kabachki.exception.VoiceStateIsNotExistException
import java.time.Duration
import java.time.Instant

// MessageCreateEvent

fun MessageCreateEvent.getMemberOrNull(): Member? = member.orElse(null)
fun MessageCreateEvent.getMemberOrThrow(): Member = getMemberOrNull() ?: throw MemberIsNotPresentException()
fun MessageCreateEvent.getUsername() = getMemberOrNull()?.userData?.username() ?: "Cumrade"
fun MessageCreateEvent.getContent() = message.content

suspend fun MessageCreateEvent.getVoiceStateOrNull() = getMemberOrNull()?.getVoiceStateOrNull()

suspend fun MessageCreateEvent.joinToMemberVoiceChannelWithProvider(audioProvider: AudioProvider) {
    val logger = LoggerFactory.getLogger(this::class.java)
    val member = getMemberOrThrow()
    try {
        member.getVoiceStateOrThrow()
            .channel
            .flatMap { channel ->
                channel.join(
                    VoiceChannelJoinSpec.builder()
                        .provider(audioProvider)
                        .build()
                )
            }.awaitSingleOrNull()
    } catch (ex: MemberIsNotPresentException) {
        logger.error("Member not found!", ex)
    }
}

suspend fun MessageCreateEvent.sendSimpleMessage(msg: String): Message =
    getMessageChannel().createMessage(msg).awaitSingle()

suspend fun MessageCreateEvent.sendSimpleMessageWithDelayedAction(
    msg: String,
    duration: Duration,
    action: (p: Message) -> Mono<out Void>
) {
    getMessageChannel().createMessage(msg)
        .delayElement(duration)
        .flatMap(action)
        .awaitSingleOrNull()
}

suspend fun MessageCreateEvent.getMessageChannel(): MessageChannel = message.channel.awaitSingle()

suspend fun MessageCreateEvent.sendComplexMessage(
    author: EmbedCreateFields.Author,
    imageUrl: String,
    title: String,
    attachmentUrl: String,
    description: String,
    listAdditionalFields: Array<EmbedCreateFields.Field>,
    footer: EmbedCreateFields.Footer
) {
    getMessageChannel().createMessage(
        EmbedCreateSpec.builder()
            .author(author)
            .title(title)
            .url(attachmentUrl)
            .description(description)
            .image(imageUrl)
            .addFields(*listAdditionalFields)
            .thumbnail(imageUrl)
            .footer(footer)
            .timestamp(Instant.now())
            .build()
    ).awaitSingleOrNull()
}

// Member

suspend fun Member.getVoiceStateOrNull() = voiceState.awaitSingleOrNull()

suspend fun Member.getVoiceStateOrThrow() = getVoiceStateOrNull() ?: throw VoiceStateIsNotExistException()

// Util

fun List<String>.checkValidUrl(): Boolean {
    val urlRegex =
        "https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)".toRegex()
    for (it in this) {
        return urlRegex.containsMatchIn(it)
    }
    return false
}