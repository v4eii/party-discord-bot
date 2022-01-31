package ussr.party.kabachki

import discord4j.core.DiscordClient
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.EmbedCreateSpec
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import ussr.party.kabachki.audio.PartyAudioManager
import ussr.party.kabachki.extension.joinToMemberVoiceChannelWithProvider
import ussr.party.kabachki.extension.sendSimpleMessage
import java.time.Duration
import java.time.Instant

const val token = "" // TODO: hide
private val discordClient = DiscordClient.builder(token).build()

private val partyAudioManager = PartyAudioManager()

fun main() {
    discordClient.withGateway {
        mono {
            it.on(MessageCreateEvent::class.java)
                .asFlow()
                .collect {
                    val message = it.message.content // TODO: replace to command interface
                    when {
                        message == "!ping"        -> {
                            it.sendSimpleMessage("Pong!")
                        }
                        message.contains("!play") -> {
                            it.message.content.split(" ").let { resources ->
                                partyAudioManager.runTrackOrAddToQueue(resources.subList(1, resources.size))
                            }
                            it.joinToMemberVoiceChannelWithProvider(partyAudioManager.provider)  // TODO first check valid voice channel, then load track
                        }
                        message == "!dosomething" -> { // TODO make better complex message, change input command
                            val imageUrl = "https://cdn.betterttv.net/emote/55028cd2135896936880fdd7/3x"
                            val phonkUrl = "https://youtu.be/8edZVkacijA"
                            it.message.channel.awaitSingle()
                                .createMessage(
                                    EmbedCreateSpec.builder()
                                        .author("а?", phonkUrl, imageUrl)
                                        .image(imageUrl)
                                        .title("Best phonk")
                                        .url(phonkUrl)
                                        .description(
                                            """
                                                            Check it out
                                                            Best phonk compilation
                                                        """.trimIndent()
                                        )
                                        .addField("Rate", "4.6/5", true)
                                        .addField("Phonk", "5.1/5", false)
                                        .thumbnail(imageUrl)
                                        .footer("provided party bot", imageUrl)
                                        .timestamp(Instant.now())
                                        .build()
                                ).awaitSingle()
                        }
                        message == "!secret"      -> {  // TODO: make executable only for developer role
                            it.message.channel.awaitSingle() // TODO: create inline fun for delayed action
                                .createMessage("secret token: $token, this message will be deleted in a minute")
                                .delayElement(Duration.ofMinutes(1))
                                .flatMap { msg -> msg.delete() }
                                .awaitSingleOrNull()
                        }
                    }
                }
        }
    }.block()
}
