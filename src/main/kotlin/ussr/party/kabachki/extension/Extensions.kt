package ussr.party.kabachki.extension

import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.EmbedCreateFields
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.VoiceChannelJoinSpec
import discord4j.discordjson.possible.Possible
import discord4j.voice.AudioProvider
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import ussr.party.kabachki.exception.MemberIsNotPresentException
import ussr.party.kabachki.exception.VoiceStateIsNotExistException
import java.time.Duration
import java.time.Instant

suspend fun ChatInputInteractionEvent.getVoiceStateOrNull() = getMemberOrNull()?.getVoiceStateOrNull()
suspend fun ChatInputInteractionEvent.getMessageChannelOrNull() = interaction.channel.awaitFirstOrNull()
suspend fun ChatInputInteractionEvent.getMessageChannelOrThrow() =
    getMessageChannelOrNull() ?: throw RuntimeException() // TODO

suspend fun ChatInputInteractionEvent.replyTo(content: String, ephemeral: Boolean = false) =
    reply().withContent(content).withEphemeral(ephemeral).awaitFirstOrNull()

fun ChatInputInteractionEvent.getMemberOrNull(): Member? = interaction.member.orElse(null)
fun ChatInputInteractionEvent.getMemberOrThrow(): Member = getMemberOrNull() ?: throw MemberIsNotPresentException()
fun ChatInputInteractionEvent.getUsername() = getMemberOrNull()?.userData?.username() ?: "Cumrade"
fun ChatInputInteractionEvent.getUserMention() = interaction.user.mention

@SuppressWarnings
fun ChatInputInteractionEvent.getOptionByNameOrNull(name: String): String? =
    getOption(name).orElse(null)
        ?.value
        ?.orElse(null)
        ?.raw

fun MessageCreateEvent.getMemberOrNull(): Member? = member.orElse(null)
fun MessageCreateEvent.getMemberOrThrow(): Member = getMemberOrNull() ?: throw MemberIsNotPresentException()
fun MessageCreateEvent.getUsername() = getMemberOrNull()?.userData?.username() ?: "Cumrade"
fun MessageCreateEvent.getMentionUsername() = getMemberOrNull()?.mention ?: "Cumrade"
fun MessageCreateEvent.getContent() = message.content

suspend fun ChatInputInteractionEvent.joinToMemberVoiceChannelWithProvider(audioProvider: AudioProvider) {
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

suspend fun MessageChannel.sendComplexMessage(
    author: EmbedCreateFields.Author? = null,
    imageUrl: String,
    title: String,
    attachmentTitleUrl: String? = null,
    description: String,
    additionalFields: Array<EmbedCreateFields.Field> = arrayOf(),
    footer: EmbedCreateFields.Footer? = null
) {
    createMessage(
        EmbedCreateSpec.builder()
            .author(author)
            .title(title)
            .urlOrNone(attachmentTitleUrl)
            .description(description)
            .thumbnail(imageUrl)
            .addFields(*additionalFields)
            .footer(footer)
            .timestamp(Instant.now())
            .build()
    ).awaitSingleOrNull()
}

fun EmbedCreateSpec.Builder.urlOrNone(url: String?) = url?.let {
    url(url)
} ?: this

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

fun <T> T.toPossible() = Possible.of(this!!)