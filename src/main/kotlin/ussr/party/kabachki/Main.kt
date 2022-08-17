package ussr.party.kabachki

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import discord4j.gateway.intent.IntentSet
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.mono
import reactor.core.publisher.Mono
import ussr.party.kabachki.command.manager.CommandRegisterManagerImpl
import ussr.party.kabachki.event.EventCollector
import ussr.party.kabachki.event.handler.impl.*
import ussr.party.kabachki.exception.LaunchException

val configHolder = mutableMapOf<String, Any>()

fun main(args: Array<String>) {
    val botToken = System.getenv("BOT_TOKEN") ?: args.firstOrNull() ?: throw LaunchException("token key is not present")
    val isDebug = System.getenv("IS_DEBUG")?.toBoolean() ?: false
    val discordClient = DiscordClient.builder(botToken).build()

    configHolder["token"] = botToken
    configHolder["isDebug"] = isDebug

    val slashCommandHandler = ChatInputInteractionEventHandler()
    val readyEventHandler = ReadyEventHandler()
    val messageCreateEventHandler = MessageCreateEventHandler()
    val presenceUpdateEventHandler = PresenceUpdateEventHandler()
    val voiceStateUpdateEventHandler = VoiceStateUpdateEventHandler()

    discordClient.gateway().setEnabledIntents(IntentSet.all()).withGateway { gateway ->
        val readyEventPublisher = EventCollector.collectOneEvent(gateway, readyEventHandler)
        val interactionEventPublisher = EventCollector.collectEvents(gateway, slashCommandHandler)
        val messageCreateEventPublisher = EventCollector.collectEvents(gateway, messageCreateEventHandler)
        val presenceUpdateEventPublisher = EventCollector.collectEvents(gateway, presenceUpdateEventHandler)
        val voiceStateUpdateEventPublisher = EventCollector.collectEvents(gateway, voiceStateUpdateEventHandler)

        val interactionEventWithResumePublisher = interactionEventPublisher.onErrorResume { interactionEventPublisher }

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
                        "skip.json",
                        "move-cumrade.json"
                    )
                )
        }
            .and(readyEventPublisher)
            .and(messageCreateEventPublisher)
            .and(interactionEventWithResumePublisher.onErrorResume { interactionEventWithResumePublisher })
            .and(presenceUpdateEventPublisher)
            .and(voiceStateUpdateEventPublisher)
            .withDebug(gateway, isDebug)
    }.block()
}

fun Mono<*>.withDebug(gateway: GatewayDiscordClient, isDebug: Boolean) =
    if (isDebug) {
        and(gateway.on(Event::class.java) {
            mono {
                launch {
                    println(it)
                }
            }
        })
    } else {
        this
    }

