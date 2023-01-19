package ussr.party.kabachki.client

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import ussr.party.kabachki.dto.ParseImageTagsRequest
import ussr.party.kabachki.dto.ParseImageTagsResponse

class VkBotClient(
    private val webClient: WebClient
) {

    suspend fun getImageTags(imageUrl: String): ParseImageTagsResponse = webClient.post()
        .uri("/api/v1/parse_image_tags")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .header("X-CSRFTOKEN", "JgeMBLwqxUfBUppVv4WugzuWydJo9VVZqI1JpkPtbxul6tmYrFwyAVjjtXJLbG2S")
        .bodyValue(ParseImageTagsRequest(imageUrl = imageUrl).also { println(ObjectMapper().writeValueAsString(it)) })
        .retrieve()
        .bodyToMono<ParseImageTagsResponse>()
        .awaitFirst()

}