package ussr.party.kabachki.bot.event.handler.impl

import org.springframework.stereotype.Component
import ussr.party.kabachki.bot.event.SendMessageEvent
import ussr.party.kabachki.bot.event.handler.EventHandler
import ussr.party.kabachki.service.MessagePublisherService

@Component
class SendMessageEventHandler(
    private val messagePublisherService: MessagePublisherService
) : EventHandler<SendMessageEvent> {
    override suspend fun handle(event: SendMessageEvent) {
        messagePublisherService.publishMessage(event.content)
    }
}