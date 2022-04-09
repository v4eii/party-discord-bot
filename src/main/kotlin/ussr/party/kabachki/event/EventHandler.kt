package ussr.party.kabachki.event

import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.mono
import ussr.party.kabachki.event.processor.EventProcessor

object EventHandler {
    inline fun <reified T : Event> collectEvent(gateway: GatewayDiscordClient, eventProcessor: EventProcessor<T>) =
        mono {
            gateway.on(T::class.java)
                .asFlow()
                .collect { eventProcessor.process(it, scope = this) }
        }

    inline fun <reified T : Event> collectOneEvent(gateway: GatewayDiscordClient, eventProcessor: EventProcessor<T>) =
        gateway.on(T::class.java) {
            mono {
                eventProcessor.process(it, scope = this)
            }
        }
}