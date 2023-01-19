package ussr.party.kabachki.bot.event.handler.impl

import discord4j.core.`object`.entity.channel.TextChannel
import discord4j.core.event.domain.lifecycle.ReadyEvent
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.stereotype.Component
import ussr.party.kabachki.bot.event.handler.EventHandler
import ussr.party.kabachki.bot.extension.getChannel
import ussr.party.kabachki.bot.extension.sendSimpleMessage

@Component
class ReadyEventHandler : EventHandler<ReadyEvent> {
    override suspend fun handle(event: ReadyEvent) {
        event.getSelfGuild()
            .getChannel<TextChannel>("spam-for-bot")
            .sendSimpleMessage("I am alive!")
    }

    private suspend fun ReadyEvent.getSelfGuild() = self.client.guilds.awaitFirst()
}