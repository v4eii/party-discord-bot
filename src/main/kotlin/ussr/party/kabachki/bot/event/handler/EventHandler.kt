package ussr.party.kabachki.bot.event.handler

import discord4j.core.event.domain.Event

fun interface EventHandler<T : Event> {
    suspend fun handle(event: T)
}