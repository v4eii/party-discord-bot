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