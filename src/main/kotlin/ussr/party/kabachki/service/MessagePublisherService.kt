package ussr.party.kabachki.service

import discord4j.common.util.Snowflake
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import ussr.party.kabachki.config.UserDataProperties
import ussr.party.kabachki.config.bot.BotElements
import ussr.party.kabachki.extension.discordMention
import ussr.party.kabachki.extension.toServerPartySystem
import ussr.party.kabachki.model.ChannelMessageDTO

@Component
@EnableConfigurationProperties(UserDataProperties::class)
class MessagePublisherService(
    private val botElements: BotElements,
    private val userDataProperties: UserDataProperties
) {

    suspend fun publishMessage(message: ChannelMessageDTO) {
        val text = message.run {
            if (userSystemId != null && partySystem != null)
                "${
                    userDataProperties.convertUserSystemId(
                        id = userSystemId,
                        systemFrom = partySystem.toServerPartySystem()
                    ).discordMention()
                } $content"
            else
                content
        }
        botElements.gatewayDiscordClient
            .rest()
            .getChannelById(Snowflake.of(937785108696551546))
            .createMessage(text)
            .awaitFirstOrNull()
    }

}