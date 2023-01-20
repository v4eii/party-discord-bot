package ussr.party.kabachki.bot.event.handler.impl

import discord4j.core.`object`.entity.channel.TextChannel
import discord4j.core.`object`.presence.Presence
import discord4j.core.event.domain.PresenceUpdateEvent
import kotlinx.coroutines.reactive.awaitFirst
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ussr.party.kabachki.bot.event.handler.EventHandler
import ussr.party.kabachki.bot.extension.getChannel
import ussr.party.kabachki.bot.extension.sendSimpleMessage

@Component
class PresenceUpdateEventHandler : EventHandler<PresenceUpdateEvent> {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val gamesReactMap = listOf(
        Triple(
            first = "Dota",
            second = """%s пиздец чел
               | выйди из доты, пожалуйста, не позорься, это хуже чем аниме
               | А как мы знаем:
            """.trimMargin(),
            third = ""
        ),
        Triple(
            first = "Path of Exile",
            second = "%s happy inventory management!",
            third = "https://tenor.com/view/yakuza-gif-19282842"
        ),
        Triple(
            first = "Genshin",
            second = "%s давай давай собирай свои амонгусы",
            third = "https://tenor.com/view/kiryu-slapping-yakuza-haruka-gif-16227101"
        ),
        Triple(
            first = "Tower of Fantasy",
            second = """%s немезида не кантрица, еб#нные донатеры!!
                |вопрос: как и почему враги меня бьют и почему не умирают?
                |на ответ пойдешь нахуй гандон
            """.trimMargin(),
            third = "https://tenor.com/view/kiryu-slapping-yakuza-haruka-gif-16227101"
        ),
        Triple(
            first = "Yakuza",
            second = "%s",
            third = "https://tenor.com/view/%D0%BA%D0%B0%D0%B2%D1%83%D0%BD-%D0%B0%D1%80%D0%B1%D1%83%D0%B7-%D0%B1%D0%B0%D0%B7%D0%B0-gif-25186538"
        ),
        Triple(
            first = "Batman",
            second = "%s так мы убьем бэтмена или как?",
            third = ""
        ),
        Triple(
            first = "Elden Ring",
            second = "%s а может не надо?",
            third = ""
        ),
        Triple(
            first = "Warframe",
            second = "%s здесь мог быть наш кибербуллинг, на всякий фу",
            third = ""
        ),
        Triple(
            first = "Kerbal",
            second = """%s так падажжи ебана, суда пару движков, суда воздухозаборников
               |смысле 1.2 маха не выжмем? да ща все 1.5 будут на
               |а чо смысле на Луну? ну давай тогда декуплеров херачь да побольше! 
            """.trimMargin(),
            third = "https://tenor.com/view/science-kerbal-xacktar-gif-20679215"
        ),
//        Triple(
//            first = "Spotify",
//            second = "%s",
//            third = "https://tenor.com/view/ddd-gif-22697176"
//        ),
        Triple(
            first = "Gunfire",
            second = "%s фурри в бой!",
            third = "https://media.discordapp.net/attachments/960928970629582918/1015667188998357112/unknown.png"
        ),
        Triple(
            first = "Forza",
            second = "%s дергаешь руль влево - поворачиваешь направо",
            third = ""
        )
    )

    override suspend fun handle(event: PresenceUpdateEvent) {
        event.run {
            val currentPresence = getPresence()
            val oldPresence = getPresence(isOld = true)
            logger.info(
                """
                    ${getUsername()}
                    oldActivity: ${oldPresence?.activities}
                    oldStatus: ${oldPresence?.status}
                    newActivity: ${currentPresence.activities}
                    newStatus: ${currentPresence.status}
                    
                """.trimIndent()
            )
            reactToChangePresence(
                currentPresence = currentPresence,
                oldPresence = oldPresence,
                responseChannel = getEventGuild().getChannel("gamesы")
            )
        }
    }

    private suspend fun PresenceUpdateEvent.reactToChangePresence(
        currentPresence: Presence,
        oldPresence: Presence,
        responseChannel: TextChannel
    ) {
        if (oldPresence.notContainsActivity("Spotify")) { // because spotify trigger change presence every track
            gamesReactMap.forEach { (gameName, text, gifUrl) ->
                if (currentPresence.containsActivity(gameName, oldPresence)) {
                    if (text.isNotBlank()) responseChannel.sendSimpleMessage(text.format(getUserMention()))
                    if (gifUrl.isNotBlank()) responseChannel.sendSimpleMessage(gifUrl)
                }
            }
        }
    }

    private suspend fun PresenceUpdateEvent.getEventGuild() = this.guild.awaitFirst()
    private suspend fun PresenceUpdateEvent.getUserMention() = this.user.awaitFirst().mention
    private suspend fun PresenceUpdateEvent.getUsername() = this.user.awaitFirst().username
    private fun PresenceUpdateEvent.getPresence(isOld: Boolean = false) =
        if (isOld) this.old.orElse(null) else this.current

    private fun Presence.containsActivity(name: String, oldActivity: Presence? = null) =
        activities.any { it.name.contains(name, ignoreCase = true) } && (oldActivity?.notContainsActivity(name) ?: true)

    private fun Presence.notContainsActivity(name: String) =
        activities.none { it.name.contains(name, ignoreCase = true) }
}