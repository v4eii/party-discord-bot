package ussr.party.kabachki.extension

import discord4j.core.`object`.VoiceState
import discord4j.core.`object`.command.ApplicationCommandInteractionOptionValue
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.`object`.entity.channel.TextChannel
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.EmbedCreateFields
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.VoiceChannelJoinSpec
import discord4j.discordjson.possible.Possible
import discord4j.voice.AudioProvider
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import ussr.party.kabachki.exception.*
import java.time.Duration
import java.time.Instant
import java.util.*

fun ChatInputInteractionEvent.getMemberOrNull(): Member? = interaction.member.orElse(null)
fun ChatInputInteractionEvent.getMemberOrThrow(): Member = getMemberOrNull() ?: throw MemberIsNotPresentException()
fun ChatInputInteractionEvent.getUsername() = getMemberOrNull()?.userData?.username() ?: "Cumrade"
fun ChatInputInteractionEvent.getUserMention() = interaction.user.mention
suspend fun ChatInputInteractionEvent.getVoiceStateOrNull() = getMemberOrNull()?.getVoiceStateOrNull()
suspend fun ChatInputInteractionEvent.getMessageChannelOrNull() = interaction.channel.awaitFirstOrNull()
suspend fun ChatInputInteractionEvent.getMessageChannelOrThrow() =
    getMessageChannelOrNull() ?: throw MessageChannelNotFoundException()

suspend fun ChatInputInteractionEvent.replyTo(content: String, ephemeral: Boolean = false) =
    reply().withContent(content).withEphemeral(ephemeral).awaitFirstOrNull()

suspend fun ChatInputInteractionEvent.getGuildOrNull() = getMemberOrThrow().guild.awaitFirstOrNull()
suspend fun ChatInputInteractionEvent.getGuildOrThrow() =
    getGuildOrNull() ?: throw DiscordElementNotExistException("guild not found")

suspend fun Member.getVoiceStateOrNull() = voiceState.awaitSingleOrNull()
suspend fun Member.getVoiceStateOrThrow() = getVoiceStateOrNull() ?: throw VoiceStateIsNotExistException()
suspend fun VoiceState.getVoiceChannelOrNull() = channel.awaitSingleOrNull()
suspend fun VoiceState.getVoiceChannelOrThrow() = getVoiceChannelOrNull() ?: throw VoiceChannelIsNotExistException()
suspend fun MessageCreateEvent.getMessageChannel(): MessageChannel = message.channel.awaitSingle()

suspend fun ChatInputInteractionEvent.isDeveloper() =
    getMemberOrThrow().roles
        .any { it.name == "Developer" }
        .awaitFirstOrNull() ?: false

@SuppressWarnings
fun ChatInputInteractionEvent.getOptionByNameOrNull(name: String): ApplicationCommandInteractionOptionValue? =
    getOption(name).orElse(null)
        ?.value
        ?.orElse(null)

@SuppressWarnings
fun ChatInputInteractionEvent.getOptionByNameOrThrow(name: String) =
    getOptionByNameOrNull(name) ?: throw OptionNotFoundException("$name not found")

suspend fun ChatInputInteractionEvent.joinToMemberVoiceChannelWithProvider(audioProvider: AudioProvider) {
    val logger = LoggerFactory.getLogger(this::class.java)
    try {
        getMemberOrThrow().getVoiceStateOrThrow()
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

fun EmbedCreateSpec.Builder.urlOrNone(url: String?) = url?.let { url(url) } ?: this

@Suppress("UNCHECKED_CAST")
suspend fun <T> Guild.getChannel(name: String) = this.channels.filter { it.name == name }.awaitFirst() as T
suspend fun TextChannel.sendSimpleMessage(text: String) = createMessage(text).awaitFirstOrNull()

fun <T> T.toPossible() = Possible.of(this!!)
fun <T> T?.toOptional() = Optional.ofNullable(this)