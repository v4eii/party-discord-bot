package ussr.party.kabachki.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ParseImageTagsRequest @JsonCreator constructor(
    @JsonProperty("image_url")
    var imageUrl: String
)

data class ParseImageTagsResponse @JsonCreator constructor(
    @JsonProperty("image_url")
    var imageUrl: String,
    var tags: List<String> = emptyList(),
    @JsonProperty("is_sin")
    var isSin: Boolean
)

data class SendMessageRequest @JsonCreator constructor(
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