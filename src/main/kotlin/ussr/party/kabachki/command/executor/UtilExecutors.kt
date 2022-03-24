package ussr.party.kabachki.command.executor

import discord4j.core.spec.EmbedCreateFields
import kotlinx.coroutines.reactive.awaitFirstOrNull
import ussr.party.kabachki.command.CommandExecutor
import ussr.party.kabachki.configHolder
import ussr.party.kabachki.extension.*
import java.time.Duration

class UtilExecutors {

    val doSomethingExecutor = CommandExecutor { event, _ ->
        val imageUrl = "https://cdn.betterttv.net/emote/55028cd2135896936880fdd7/3x"
        val phonkUrl = "https://youtu.be/8edZVkacijA"
        event.sendComplexMessage(
            EmbedCreateFields.Author.of("а?", phonkUrl, imageUrl),
            imageUrl,
            "Best phonk",
            phonkUrl,
            """
                                    Check it out
                                    Best phonk compilation
                                """.trimIndent(),
            arrayOf(
                EmbedCreateFields.Field.of("Rate", "4.6/5", true),
                EmbedCreateFields.Field.of("Phonk", "5.1/5", false)
            ),
            EmbedCreateFields.Footer.of("provided party bot", imageUrl)
        )
    }

    val secretExecutor = CommandExecutor { event, params ->
        event.getMemberOrThrow().roles.filter { it.name == "Developer" }.awaitFirstOrNull()?.let {
            val delay = params.getOrElse(0) { "15" }.toLong()
            event.sendSimpleMessageWithDelayedAction(
                "secret token: ${configHolder["token"]}, this message will be deleted in a $delay sec",
                Duration.ofSeconds(delay)
            ) { msg -> msg.delete() }
        } ?: event.sendSimpleMessage("Hey ${event.getUsername()}, you are not developer")
    }

    val helpExecutor = CommandExecutor { event, _ ->
        event.sendSimpleMessage(
            """
            Все команды начинаются с '!'
            'secret' - для разработчиков, вернет токен бота, спустя таймаут сообщение с токеном будет удалено
            'play' - запуск трека с ютуба, возможно откуда-то еще, но пока с ютуба, (ex. !play <url> [<urls...>])
            'skip' - пропустить текущий трек или переключить на следующий по индексу, (ex. !skip [index]) 
            'pause' - приостановить текущий трек
            'resume' - продолжить приостановленный трек
            'queue' - вывести весь текущий плейлист с порядковыми номерами (удобно для !skip)
            'list' - аналог queue, для тех кому сложно писать и выговаривать
            'doSomething' - просто выводит комплексное сообщение
            'ping' - проверка работоспособности бота
            'help' - рекурсия
        """.trimIndent()
        )
    }

}