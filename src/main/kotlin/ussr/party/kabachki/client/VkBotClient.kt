package ussr.party.kabachki.client

import discord4j.core.`object`.entity.Member
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import ussr.party.kabachki.config.UserDataProperties
import ussr.party.kabachki.consts.PartySystem
import ussr.party.kabachki.dto.ParseImageTagsRequest
import ussr.party.kabachki.dto.ParseImageTagsResponse
import ussr.party.kabachki.dto.SendMessageRequest

class VkBotClient(
    private val webClient: WebClient,
    private val userDataProperties: UserDataProperties
) {

    suspend fun getImageTags(imageUrls: List<String>): ParseImageTagsResponse = webClient.post()
        .uri("/api/v1/parse_image_tags")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(ParseImageTagsRequest(imageUrls = imageUrls))
        .retrieve()
        .bodyToMono<ParseImageTagsResponse>()
        .awaitFirst()

    suspend fun sendMessageToVk(
        messageText: String,
        chatId: Long = 1,
        fromChat: Boolean = true,
        tagId: Boolean = false,
        member: Member
    ) {
        val userId = userDataProperties.convertUserSystemId(
            id = member.id.asString(),
            systemFrom = PartySystem.DISCORD,
            systemTo = PartySystem.VK
        )
        webClient.post()
            .uri("/api/v1/send_message")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(
                SendMessageRequest(
                    messageText = messageText,
                    userId = userId.toLong(),
                    chatId = chatId,
                    fromChat = fromChat,
                    tagId = tagId
                )
            )
            .retrieve()
            .toBodilessEntity()
            .awaitFirstOrNull()
    }

}