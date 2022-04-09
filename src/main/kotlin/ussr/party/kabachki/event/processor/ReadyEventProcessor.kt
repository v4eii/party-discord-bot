package ussr.party.kabachki.event.processor

import discord4j.core.`object`.entity.channel.TextChannel
import discord4j.core.event.domain.lifecycle.ReadyEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirst
import ussr.party.kabachki.extension.getChannel
import ussr.party.kabachki.extension.sendSimpleMessage

class ReadyEventProcessor : EventProcessor<ReadyEvent> {
    override fun process(event: ReadyEvent, scope: CoroutineScope): Job =
        scope.launch {
            event.getSelfGuild()
                .getChannel<TextChannel>("spam-for-bot")
                .sendSimpleMessage("I am alive!")
        }

    private suspend fun ReadyEvent.getSelfGuild() = self.client.guilds.awaitFirst()
}