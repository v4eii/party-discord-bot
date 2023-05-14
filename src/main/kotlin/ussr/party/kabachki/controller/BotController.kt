package ussr.party.kabachki.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ussr.party.kabachki.api.V1Api
import ussr.party.kabachki.bot.event.SendMessageEvent
import ussr.party.kabachki.config.bot.BotElements
import ussr.party.kabachki.model.ChannelMessageDTO

@RestController
class BotController(
    private val botElements: BotElements
) : V1Api {
    override fun sendDiscordMessage(channelMessageDTO: ChannelMessageDTO): ResponseEntity<Unit> =
        ResponseEntity.noContent().build<Unit>().also {
            botElements.gatewayDiscordClient
                .eventDispatcher
                .publish(
                    SendMessageEvent(
                        botElements.gatewayDiscordClient,
                        botElements.shardInfo,
                        channelMessageDTO
                    )
                )
        }
}