package ussr.party.kabachki.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ParseImageTagsRequest(
    @JsonProperty("image_urls")
    var imageUrls: List<String>
)

data class ParseImageTagsResponse(
    @JsonProperty("image_urls")
    var imageUrl: List<String>,
    var tags: List<List<String>> = emptyList(),
    @JsonProperty("is_sin")
    var isSin: Boolean
)

data class SendMessageRequest(
    @JsonProperty("message_text")
    var messageText: String,
    @JsonProperty("user_id")
    var userId: Long,
    @JsonProperty("chat_id")
    var chatId: Long?,
    @JsonProperty("from_chat")
    var fromChat: Boolean,
    @JsonProperty("tag_id")
    var tagId: Boolean
)