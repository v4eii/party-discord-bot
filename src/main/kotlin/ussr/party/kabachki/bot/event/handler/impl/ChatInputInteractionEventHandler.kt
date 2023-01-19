package ussr.party.kabachki.bot.event.handler.impl

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.springframework.stereotype.Component
import ussr.party.kabachki.bot.command.Command
import ussr.party.kabachki.bot.command.impl.*
import ussr.party.kabachki.bot.event.handler.EventHandler

@Component
class ChatInputInteractionEventHandler(
    pingCommand: PingCommand,
    secretCommand: SecretCommand,
    moveCumradeCommand: MoveCumradeCommand,
    weatherCommand: WeatherCommand,
    playCommand: PlayCommand,
    skipCommand: SkipCommand,
    pauseCommand: PauseCommand,
    resumeCommand: ResumeCommand,
    listCommand: ListCommand
) : EventHandler<ChatInputInteractionEvent> {

    private val commandList: List<Command> =
        listOf(
            pingCommand,
            secretCommand,
            moveCumradeCommand,
            weatherCommand,
            playCommand,
            skipCommand,
            pauseCommand,
            resumeCommand,
            listCommand,
        )

    override suspend fun handle(event: ChatInputInteractionEvent) {
        commandList.first { it.getName() == event.commandName }.executeCommand(event)
    }
}