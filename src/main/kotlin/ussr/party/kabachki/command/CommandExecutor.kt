package ussr.party.kabachki.command

import discord4j.core.event.domain.message.MessageCreateEvent

fun interface CommandExecutor<T> {
    suspend fun executeCommand(event: MessageCreateEvent, additionalParam: List<String>): T
}