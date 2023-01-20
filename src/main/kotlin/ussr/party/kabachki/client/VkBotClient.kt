package ussr.party.kabachki.client

import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import ussr.party.kabachki.dto.ParseImageTagsRequest
import ussr.party.kabachki.dto.ParseImageTagsResponse
import ussr.party.kabachki.dto.SendMessageRequest

class VkBotClient(
    private val webClient: WebClient
) {

    //TODO remove to conf
    val vkUsers = mapOf(
        "f" to "216144521",
        "p" to "203664038",
        "v" to "121788102",
        "m" to "187127322"
    )

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
        tagId: Boolean = false
    ) {
        webClient.post()
            .uri("/api/v1/send_message")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(
                SendMessageRequest(
                    messageText = messageText,
                    userId = vkUsers["p"]?.toLong() ?: 0,
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