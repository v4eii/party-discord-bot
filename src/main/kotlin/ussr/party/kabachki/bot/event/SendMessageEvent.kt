package ussr.party.kabachki.bot.event

import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import discord4j.gateway.ShardInfo
import ussr.party.kabachki.model.ChannelMessageDTO

class SendMessageEvent(
    gatewayDiscordClient: GatewayDiscordClient,
    shardInfo: ShardInfo,
    val messageDTO: ChannelMessageDTO
) : Event(gatewayDiscordClient, shardInfo)