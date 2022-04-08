package ussr.party.kabachki

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.entity.channel.TextChannel
import discord4j.core.event.domain.Event
import discord4j.core.event.domain.PresenceUpdateEvent
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.gateway.intent.IntentSet
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import reactor.core.publisher.Mono
import ussr.party.kabachki.command.manager.CommandRegisterManagerImpl
import ussr.party.kabachki.command.processor.CommandProcessorImpl
import ussr.party.kabachki.exception.LaunchException
import ussr.party.kabachki.extension.sendSimpleMessage

private val commandProcessorImpl = CommandProcessorImpl()
val configHolder = mutableMapOf<String, Any>()

fun main(args: Array<String>) {
    val botToken = System.getenv("BOT_TOKEN") ?: args.firstOrNull() ?: throw LaunchException("token key is not present")
    val isDebug = System.getenv("IS_DEBUG")?.toBoolean() ?: false
    val discordClient = DiscordClient.builder(botToken).build()
    configHolder["token"] = botToken
    configHolder["isDebug"] = isDebug

    discordClient.gateway().setEnabledIntents(IntentSet.all()).withGateway { gateway ->
        val interactionMono = mono {
            gateway.on(ChatInputInteractionEvent::class.java)
                .asFlow()
                .collect { launch { commandProcessorImpl.handle(it) } }
        }
        val interactionMonoWithResume = interactionMono.onErrorResume { interactionMono }

        mono {
            CommandRegisterManagerImpl(gateway.restClient)
                .registerCommands(
                    fileCommands = listOf(
                        "list.json",
                        "pause.json",
                        "ping.json",
                        "play.json",
                        "resume.json",
                        "secret.json",
                        "skip.json"
                    )
                )
        }.and(
            gateway.on(ReadyEvent::class.java) {
                mono {
                    launch {
                        it.self
                            .client
                            .guilds
                            .flatMap { it.channels }
                            .filter { it.name == "spam-for-bot" }
                            .flatMap { (it as TextChannel).createMessage("I am alive!") } // TODO improve
                            .awaitFirstOrNull()
                    }
                }
            }
        ).and(
            mono {
                gateway.on(MessageCreateEvent::class.java)
                    .asFlow()
                    .collect { launch { if (it.message.content != "Hihi haha") it.sendSimpleMessage("Hihi haha") } }
            }
        ).and(
            interactionMonoWithResume.onErrorResume { interactionMonoWithResume }
        ).and(
            mono {
                gateway.on(PresenceUpdateEvent::class.java)
                    .asFlow()
                    .collect {
                        launch {
                            (it.guild.awaitFirst()
                                .channels
                                .filter { it.name == "spam-for-bot" }
                                .awaitFirst() as TextChannel)
                                .createMessage(
                                    """
                                        ${it.user.awaitFirst().username}
                                         play in 
                                         ${it.current.activity.orElse(null)?.name}
                                    """
                                )
                                .awaitFirstOrNull()

                            if (it.current.activities.any { it.name.contains("Dota", ignoreCase = true) })
                                (it.guild.awaitFirst()
                                    .channels
                                    .filter { it.name == "gamesы" }
                                    .awaitFirst() as TextChannel)
                                    .createMessage("${it.user.awaitFirst().mention} пиздец чел, выйди из доты, пожалуйста, не позорься")
                                    .awaitFirstOrNull()
                        }
                    } // TODO improve!!!!
            }
        ).withDebug(gateway, isDebug)
    }.block()
}

fun Mono<*>.withDebug(gateway: GatewayDiscordClient, isDebug: Boolean) =
    if (isDebug) {
        this.and(gateway.on(Event::class.java) {
            mono {
                launch {
                    println(it)
                }
            }
        })
    } else {
        this
    }

