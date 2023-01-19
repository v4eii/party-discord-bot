package ussr.party.kabachki.bot.event.handler.impl

import discord4j.core.`object`.entity.Attachment
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component
import ussr.party.kabachki.bot.event.handler.EventHandler
import ussr.party.kabachki.bot.extension.getMessageChannel
import ussr.party.kabachki.client.VkBotClient
import ussr.party.kabachki.exception.MemberIsNotPresentException

@Component
class MessageCreateEventHandler(
    private val vkBotClient: VkBotClient
) : EventHandler<MessageCreateEvent> {

    val badWordMessage = """Аниме для мужелюбов ¯\_(ツ)_/¯"""

    override suspend fun handle(event: MessageCreateEvent) {
        event.run {
            if (isContainsImage()) {
                val imageTags = vkBotClient.getImageTags(getImages())
                if (imageTags.isSin || imageTags.tags.any { it.isContainsBadWord() })
                    sendSimpleMessage(badWordMessage)
            } else {
                val content = message.content
                if (content.isNotBlank() && content.isContainsBadWord() && content != badWordMessage)
                    sendSimpleMessage(badWordMessage)
            }
        }
    }

    private fun String.isContainsBadWord(): Boolean =
        this.split(" ").any {
            it.length >= 5 &&
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

    private fun isImageContentType(it: Attachment) = it.contentType.orElse("").contains("image")

    private fun MessageCreateEvent.isContainsImage() = message.attachments.any(::isImageContentType)
    private fun MessageCreateEvent.getImages() =
        message.attachments.filter(::isImageContentType).map(Attachment::getUrl).first() // todo first() temp
}