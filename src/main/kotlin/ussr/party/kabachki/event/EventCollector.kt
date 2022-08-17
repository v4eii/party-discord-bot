package ussr.party.kabachki.event

import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.mono
import ussr.party.kabachki.event.handler.EventHandler

object EventCollector {
    inline fun <reified T : Event> collectEvents(gateway: GatewayDiscordClient, eventHandler: EventHandler<T>) =
        mono {
            gateway.on(T::class.java)
                .asFlow()
                .collect {
                    launch {
                        eventHandler.handle(it)
                    }
                }
        }

    inline fun <reified T : Event> collectOneEvent(gateway: GatewayDiscordClient, eventHandler: EventHandler<T>) =
        gateway.on(T::class.java) {
            mono {
                launch {
                    eventHandler.handle(it)
                }
            }
        }
}