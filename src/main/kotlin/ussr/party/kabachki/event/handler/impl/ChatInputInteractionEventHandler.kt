package ussr.party.kabachki.event.handler.impl

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import ussr.party.kabachki.command.processor.CommandProcessorImpl
import ussr.party.kabachki.event.handler.EventHandler

class ChatInputInteractionEventHandler(
    private val commandProcessorImpl: CommandProcessorImpl
) : EventHandler<ChatInputInteractionEvent> {
    override suspend fun handle(event: ChatInputInteractionEvent) {
        commandProcessorImpl.handle(event)
    }
}