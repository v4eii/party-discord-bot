package ussr.party.kabachki.event.processor

import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import ussr.party.kabachki.exception.MemberIsNotPresentException
import ussr.party.kabachki.extension.getMessageChannel

class MessageCreateEventProcessor : EventProcessor<MessageCreateEvent> {

    override fun process(event: MessageCreateEvent, scope: CoroutineScope) =
        scope.launch {
            val content = event.message.content
            val badWordMessage = """Аниме для мужелюбов ¯\_(ツ)_/¯"""
            if (content.isNotBlank() && content.isContainsBadWord() && content != badWordMessage)
                event.sendSimpleMessage(badWordMessage)
        }

    private fun String.isContainsBadWord(): Boolean =
        this.split(" ").any {
            checkFirstBadWordLetter(it[0]) &&
                    checkSecondBadWordLetter(it[1]) &&
                    checkThirdBadWordLetter(it[2]) &&
                    checkFourthBadWordLetter(it[3]) &&
                    checkFifthBadWordLetter(it[4])

        }

    private fun checkFirstBadWordLetter(letter: Char) =
        letter.equals('а', true) ||
                letter.equals('о', true) ||
                letter.equals('a', true) ||
                letter.equals('o', true)

    private fun checkSecondBadWordLetter(letter: Char) =
        letter.equals('н', true) || letter.equals('n', true)

    private fun checkThirdBadWordLetter(letter: Char) =
        letter.equals('и', true) ||
                letter.equals('я', true) ||
                letter.equals('i', true) ||
                letter.equals('е', true)

    private fun checkFourthBadWordLetter(letter: Char) =
        letter.equals('м', true) || letter.equals('m', true)

    private fun checkFifthBadWordLetter(letter: Char) =
        letter.equals('е', true) || letter.equals('e', true)

    private suspend fun MessageCreateEvent.sendSimpleMessage(msg: String): Message =
        getMessageChannel().createMessage(msg).awaitSingle()

    private fun MessageCreateEvent.getMemberOrNull(): Member? = member.orElse(null)
    private fun MessageCreateEvent.getMemberOrThrow(): Member = getMemberOrNull() ?: throw MemberIsNotPresentException()
    private fun MessageCreateEvent.getUsername() = getMemberOrNull()?.userData?.username() ?: "Cumrade"
    private fun MessageCreateEvent.getMentionUsername() = getMemberOrNull()?.mention ?: "Cumrade"
    private fun MessageCreateEvent.getContent() = message.content
}