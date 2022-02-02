package ussr.party.kabachki.command

import discord4j.core.event.domain.message.MessageCreateEvent

interface CommandProcessor {
    suspend fun parseCommand(event: MessageCreateEvent): Any
    fun String.isCommand(): Boolean
}