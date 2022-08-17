package ussr.party.kabachki.event.processor

import discord4j.core.`object`.entity.channel.TextChannel
import discord4j.core.`object`.presence.Presence
import discord4j.core.event.domain.PresenceUpdateEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirst
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ussr.party.kabachki.extension.getChannel
import ussr.party.kabachki.extension.sendSimpleMessage

class PresenceUpdateEventProcessor : EventProcessor<PresenceUpdateEvent> {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun process(event: PresenceUpdateEvent, scope: CoroutineScope) =
        scope.launch {
            val oldPresence = event.getPresence(isOld = true)
            val currentPresence = event.getPresence()
            logger.info(
                """
                    ${event.getUsername()}
                    oldActivity: ${oldPresence.activities}
                    oldStatus: ${oldPresence.status}
                    newActivity: ${currentPresence.activities}
                    newStatus: ${currentPresence.status}
                """.trimIndent()
            )

            val responseChannel = event.getEventGuild().getChannel<TextChannel>("gamesы")

            if (oldPresence.notContainsActivity("Spotify")) { // because spotify trigger change presence every track
                when {
                    currentPresence.containsActivity("Dota") -> {
                        responseChannel.sendSimpleMessage(
                            """${event.getUserMention()} пиздец чел
                            | выйди из доты, пожалуйста, не позорься, это хуже чем аниме
                            | А как мы знаем:
                         """.trimMargin()
                        )
                    }
                    currentPresence.containsActivity("Path of Exile") -> {
                        responseChannel.sendSimpleMessage(
                            "${event.getUserMention()} happy inventory management!"
                        )
                        responseChannel.sendSimpleMessage(
                            "https://tenor.com/view/yakuza-gif-19282842"
                        )
                    }
                    currentPresence.containsActivity("Genshin") ||
                            (currentPresence.containsActivity("Tower of Fantasy") && oldPresence.notContainsActivity("Tower of Fantasy")) -> {
                        responseChannel.sendSimpleMessage(
                            """${event.getUserMention()} немезида не кантрица, еб#нные донатеры!!
                                |вопрос: как и почему враги меня бьют и почему не умирают?
                                |на ответ пойдешь нахуй гандон
                                |
                                |или не то? ну тогда амонгусов иди собери
                            """.trimMargin()
                        )
                        responseChannel.sendSimpleMessage(
                            "https://tenor.com/view/kiryu-slapping-yakuza-haruka-gif-16227101"
                        )
                    }
                    currentPresence.containsActivity("Yakuza") ->
                        responseChannel.sendSimpleMessage(
                            "https://tenor.com/view/%D0%BA%D0%B0%D0%B2%D1%83%D0%BD-%D0%B0%D1%80%D0%B1%D1%83%D0%B7-%D0%B1%D0%B0%D0%B7%D0%B0-gif-25186538"
                        )
                    currentPresence.containsActivity("Batman") -> {
                        responseChannel.sendSimpleMessage(
                            "${event.getUserMention()} так мы убьем бэтмена или как?"
                        )
                    }
                    currentPresence.containsActivity("Elden Ring") ->
                        responseChannel.sendSimpleMessage("${event.getUserMention()} а может не надо?")
                    currentPresence.containsActivity("Warframe") ->
                        responseChannel.sendSimpleMessage("${event.getUserMention()} здесь мог быть наш кибербуллинг, на всякий фу")
                }
            }
        }

    private suspend fun PresenceUpdateEvent.getEventGuild() = this.guild.awaitFirst()
    private suspend fun PresenceUpdateEvent.getUserMention() = this.user.awaitFirst().mention
    private suspend fun PresenceUpdateEvent.getUsername() = this.user.awaitFirst().username
    private fun PresenceUpdateEvent.getPresence(isOld: Boolean = false) =
        if (isOld) this.old.orElse(null) else this.current

    private fun Presence.containsActivity(name: String) = activities.any { it.name.contains(name, ignoreCase = true) }
    private fun Presence.notContainsActivity(name: String) =
        activities.none { it.name.contains(name, ignoreCase = true) }
}