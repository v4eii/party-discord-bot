package ussr.party.kabachki.bot.event.handler.impl

import discord4j.core.`object`.entity.channel.TextChannel
import discord4j.core.event.domain.VoiceStateUpdateEvent
import discord4j.discordjson.Id
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.stereotype.Component
import ussr.party.kabachki.bot.event.handler.EventHandler
import ussr.party.kabachki.bot.extension.getChannel
import ussr.party.kabachki.bot.extension.sendSimpleMessage

@Component
class VoiceStateUpdateEventHandler : EventHandler<VoiceStateUpdateEvent> {
    override suspend fun handle(event: VoiceStateUpdateEvent) {
        val currentChannelId = event.current.data.channelId().orElse(Id.of(0)).asString()
        if (currentChannelId == "992829909976227953" && (event.isJoinEvent || event.isMoveEvent)) {
            val mention = event.current.member.awaitFirst().mention
            val textChannel = event.current
                .guild
                .awaitFirst()
                .getChannel<TextChannel>("info")
            textChannel.sendSimpleMessage("$mention короче асуждаем (или нет?)")
            textChannel.sendSimpleMessage("https://tenor.com/view/no-anime-hate-anime-frick-anime-mad-angry-gif-24827257")
        }
    }
}