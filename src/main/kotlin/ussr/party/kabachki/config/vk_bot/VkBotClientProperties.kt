package ussr.party.kabachki.config.vk_bot

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("client.vk")
data class VkBotClientProperties(
    val baseUrl: String
)