package ussr.party.kabachki.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent

interface Command {
    fun getName(): String
    suspend fun executeCommand(event: ChatInputInteractionEvent)
}