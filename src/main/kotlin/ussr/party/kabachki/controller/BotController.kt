package ussr.party.kabachki.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ussr.party.kabachki.bot.event.SendMessageEvent
import ussr.party.kabachki.config.bot.BotElements

@RestController
class BotController(
    private val botElements: BotElements
) {
    @PostMapping("/api/v1/send-message")
    suspend fun sendMessage(@RequestBody sendMessageDTO: SendMessageDTO) {
        botElements.gatewayDiscordClient
            .eventDispatcher
            .publish(
                SendMessageEvent(
                    botElements.gatewayDiscordClient,
                    botElements.shardInfo,
                    sendMessageDTO.content
                )
            )
    }

}

data class SendMessageDTO(
    val content: String
)