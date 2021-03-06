package ussr.party.kabachki

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import discord4j.gateway.intent.IntentSet
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.mono
import reactor.core.publisher.Mono
import ussr.party.kabachki.command.manager.CommandRegisterManagerImpl
import ussr.party.kabachki.command.processor.CommandProcessorImpl
import ussr.party.kabachki.event.EventHandler
import ussr.party.kabachki.event.processor.ChatInputInteractionEventProcessor
import ussr.party.kabachki.event.processor.MessageCreateEventProcessor
import ussr.party.kabachki.event.processor.PresenceUpdateEventProcessor
import ussr.party.kabachki.event.processor.ReadyEventProcessor
import ussr.party.kabachki.exception.LaunchException

val configHolder = mutableMapOf<String, Any>()

fun main(args: Array<String>) {
    val botToken = System.getenv("BOT_TOKEN") ?: args.firstOrNull() ?: throw LaunchException("token key is not present")
    val isDebug = System.getenv("IS_DEBUG")?.toBoolean() ?: false
    val discordClient = DiscordClient.builder(botToken).build()

    configHolder["token"] = botToken
    configHolder["isDebug"] = isDebug

    val slashCommandProcessor = ChatInputInteractionEventProcessor(CommandProcessorImpl())
    val readyEventProcessor = ReadyEventProcessor()
    val messageCreateEventProcessor = MessageCreateEventProcessor()
    val presenceUpdateEventProcessor = PresenceUpdateEventProcessor()

    discordClient.gateway().setEnabledIntents(IntentSet.all()).withGateway { gateway ->
        val readyEventPublisher = EventHandler.collectOneEvent(gateway, readyEventProcessor)
        val interactionEventPublisher = EventHandler.collectEvent(gateway, slashCommandProcessor)
        val messageCreateEventPublisher = EventHandler.collectEvent(gateway, messageCreateEventProcessor)
        val presenceUpdateEventPublisher = EventHandler.collectEvent(gateway, presenceUpdateEventProcessor)

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
                        "skip.json"
                    )
                )
        }
            .and(readyEventPublisher)
            .and(messageCreateEventPublisher)
            .and(interactionEventWithResumePublisher.onErrorResume { interactionEventWithResumePublisher })
            .and(presenceUpdateEventPublisher)
            .withDebug(gateway, isDebug)
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

