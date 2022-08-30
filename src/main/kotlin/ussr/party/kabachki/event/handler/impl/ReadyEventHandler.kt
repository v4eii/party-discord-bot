package ussr.party.kabachki.event.handler.impl

import discord4j.core.`object`.entity.channel.TextChannel
import discord4j.core.event.domain.lifecycle.ReadyEvent
import kotlinx.coroutines.reactive.awaitFirst
import ussr.party.kabachki.event.handler.EventHandler
import ussr.party.kabachki.extension.getChannel
import ussr.party.kabachki.extension.sendSimpleMessage

class ReadyEventHandler : EventHandler<ReadyEvent> {
    override suspend fun handle(event: ReadyEvent) {
        event.getSelfGuild()
            .getChannel<TextChannel>("spam-for-bot")
            .sendSimpleMessage("I am alive!")
    }

    private suspend fun ReadyEvent.getSelfGuild() = self.client.guilds.awaitFirst()
}