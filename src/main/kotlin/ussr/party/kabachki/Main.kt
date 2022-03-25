package ussr.party.kabachki

import discord4j.core.DiscordClient
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.mono
import ussr.party.kabachki.command.manager.CommandRegisterManagerImpl
import ussr.party.kabachki.command.processor.CommandProcessorImpl
import ussr.party.kabachki.exception.LaunchException

private val commandProcessorImpl = CommandProcessorImpl()
val configHolder = mutableMapOf<String, Any>()

fun main(args: Array<String>) {
    val botToken = System.getenv("BOT_TOKEN") ?: args.firstOrNull() ?: throw LaunchException("token key is not present")
    val discordClient = DiscordClient.builder(botToken).build()
    configHolder["token"] = botToken

    discordClient.withGateway { gateway ->
        mono {
            CommandRegisterManagerImpl(gateway.restClient)
                .registerCommands(
                    fileCommands = listOf(
                        "list.json",
                        "pause.json",
                        "ping.json",
                        "play.json",
                        "resume.json",
                        "secret.json",
                        "skip.json"
                    )
                )

            gateway.on(ChatInputInteractionEvent::class.java)
                .asFlow()
                .collect { launch { commandProcessorImpl.handle(it) } }
        }
//            .and(
//            mono {
//                gateway.on(MessageCreateEvent::class.java)
//                    .asFlow()
//                    .collect { launch { commandProcessorImpl.parseCommand(it) } }
//            }
//        )
//            .and(
//            mono {
//                gateway.on(ReadyEvent::class.java)
//                    .asFlow()
//                    .collect { launch { println("${it.self.username} alive") } }
//            }
//        ).and(
//            mono {
//                gateway.on(ChatInputInteractionEvent::class.java)
//                    .asFlow()
//                    .collect { launch { if(it.commandName == "ping") it.reply("Pong!") } }
//            }
//        )
    }.block()
}
