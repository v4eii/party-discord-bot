package ussr.party.kabachki.config.vk_bot

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import ussr.party.kabachki.client.VkBotClient
import ussr.party.kabachki.config.UserDataProperties
import ussr.party.kabachki.extension.getLogger

@Configuration
@EnableConfigurationProperties(VkBotClientProperties::class)
class VkBotClientConfig(
    private val vkBotClientProperties: VkBotClientProperties,
    private val userDataProperties: UserDataProperties
) {

    val logger = getLogger<VkBotClientConfig>()

    @Bean
    fun vkBotClient() = vkBotClientProperties.run {
        VkBotClient(
            webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .filter { request, next ->
                    logger.info("URL: ${request.url()}")
                    logger.info("Headers: ${request.headers()}")
                    logger.info("Body: ${request.body()}")
                    next.exchange(request)
                }
                .build(),
            userDataProperties = userDataProperties
        )
    }

}