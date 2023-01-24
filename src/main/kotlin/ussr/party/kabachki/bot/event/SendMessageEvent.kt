package ussr.party.kabachki.bot.event

import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import discord4j.gateway.ShardInfo

class SendMessageEvent(
    gatewayDiscordClient: GatewayDiscordClient,
    shardInfo: ShardInfo,
    val content: String
) : Event(gatewayDiscordClient, shardInfo)