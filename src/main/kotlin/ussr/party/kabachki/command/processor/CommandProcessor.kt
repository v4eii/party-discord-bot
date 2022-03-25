package ussr.party.kabachki.command.processor

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent

interface CommandProcessor {
    suspend fun handle(event: ChatInputInteractionEvent)
}