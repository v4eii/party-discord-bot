package ussr.party.kabachki.config.bot

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("bot")
data class BotProperties(
    val token: String
)