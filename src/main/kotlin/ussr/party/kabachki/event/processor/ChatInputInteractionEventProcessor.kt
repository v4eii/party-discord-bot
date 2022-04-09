package ussr.party.kabachki.event.processor

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ussr.party.kabachki.command.processor.CommandProcessorImpl

class ChatInputInteractionEventProcessor(
    private val commandProcessorImpl: CommandProcessorImpl
) : EventProcessor<ChatInputInteractionEvent> {
    override fun process(event: ChatInputInteractionEvent, scope: CoroutineScope): Job {
        return scope.launch { commandProcessorImpl.handle(event) }
    }
}