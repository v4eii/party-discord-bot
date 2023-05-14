package ussr.party.kabachki.config.bot

import discord4j.core.GatewayDiscordClient
import discord4j.gateway.ShardInfo
import org.springframework.context.annotation.Configuration

@Configuration
class BotElements {
    lateinit var gatewayDiscordClient: GatewayDiscordClient
    lateinit var shardInfo: ShardInfo
}