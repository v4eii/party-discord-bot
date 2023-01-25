package ussr.party.kabachki.service

import discord4j.common.util.Snowflake
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Component
import ussr.party.kabachki.config.bot.BotElements

@Component
class MessagePublisherService(
    private val botElements: BotElements
) {
    //    Snowflake{937689943814328320} guild id
    //    Snowflake{937785108696551546} channelId
    suspend fun publishMessage(message: String) {
        botElements.gatewayDiscordClient
            .rest()
            .getChannelById(Snowflake.of(937785108696551546))
            .createMessage(message)
            .awaitFirstOrNull()
    }

}