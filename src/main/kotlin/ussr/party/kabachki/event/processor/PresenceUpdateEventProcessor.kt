package ussr.party.kabachki.event.processor

import discord4j.core.`object`.entity.channel.TextChannel
import discord4j.core.event.domain.PresenceUpdateEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirst
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ussr.party.kabachki.extension.getChannel
import ussr.party.kabachki.extension.sendSimpleMessage

class PresenceUpdateEventProcessor : EventProcessor<PresenceUpdateEvent> {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)
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

            if (currentPresence.activities.any { it.name.contains("Dota", ignoreCase = true) }) {
                event.getEventGuild()
                    .getChannel<TextChannel>("gamesы")
                    .sendSimpleMessage(
                        """${event.getUserMention()} пиздец чел
                            | выйди из доты, пожалуйста, не позорься, это хуже чем аниме
                            | А как мы знаем:
                         """.trimMargin()
                    )
            }
        }

    private suspend fun PresenceUpdateEvent.getEventGuild() = this.guild.awaitFirst()
    private suspend fun PresenceUpdateEvent.getUserMention() = this.user.awaitFirst().mention
    private suspend fun PresenceUpdateEvent.getUsername() = this.user.awaitFirst().username
    private fun PresenceUpdateEvent.getPresence(isOld: Boolean = false) =
        if (isOld) this.old.orElse(null) else this.current
}