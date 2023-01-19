package ussr.party.kabachki.bot.event

import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ussr.party.kabachki.bot.event.handler.EventHandler
import ussr.party.kabachki.extension.getLogger

object EventCollector {
    val logger: Logger = getLogger<EventCollector>()

    inline fun <reified T : Event> collectEvents(gateway: GatewayDiscordClient, eventHandler: EventHandler<T>): Mono<Unit> {
        val mono = mono {
            gateway.on(T::class.java)
                .asFlow()
                .collect {
                    launch {
                        eventHandler.handle(it)
                    }
                }
        }
        return mono.onErrorResume {
            logger.error(it.message, it)
            mono
        }
    }

    inline fun <reified T : Event> collectOneEvent(gateway: GatewayDiscordClient, eventHandler: EventHandler<T>): Flux<Job> {
        val onMono = gateway.on(T::class.java) {
            mono {
                launch {
                    eventHandler.handle(it)
                }
            }
        }
        return onMono.onErrorResume {
            logger.error(it.message, it)
            onMono
        }
    }
}