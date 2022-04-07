package ussr.party.kabachki

import discord4j.core.DiscordClient
import discord4j.core.`object`.entity.channel.TextChannel
import discord4j.core.event.domain.PresenceUpdateEvent
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import ussr.party.kabachki.command.manager.CommandRegisterManagerImpl
import ussr.party.kabachki.command.processor.CommandProcessorImpl
import ussr.party.kabachki.exception.LaunchException
import ussr.party.kabachki.extension.sendSimpleMessage

private val commandProcessorImpl = CommandProcessorImpl()
val configHolder = mutableMapOf<String, Any>()

fun main(args: Array<String>) {
    val botToken = System.getenv("BOT_TOKEN") ?: args.firstOrNull() ?: throw LaunchException("token key is not present")
    val discordClient = DiscordClient.builder(botToken).build()
    configHolder["token"] = botToken

    discordClient.withGateway { gateway ->
        val interactionMono = mono {
            gateway.on(ChatInputInteractionEvent::class.java)
                .asFlow()
                .collect { launch { commandProcessorImpl.handle(it) } }
        }
        val interactionMonoWithResume = interactionMono.onErrorResume { interactionMono }

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
        }.and(
            gateway.on(ReadyEvent::class.java) {
                mono {
                    launch {
                        it.self
                            .client
                            .guilds
                            .flatMap { it.channels }
                            .filter { it.name == "spam-for-bot" }
                            .flatMap { (it as TextChannel).createMessage("I am alive!") } // TODO improve
                            .awaitFirstOrNull()
                    }
                }
            }
        ).and(
            mono {
                gateway.on(MessageCreateEvent::class.java)
                    .asFlow()
                    .collect { launch { if (it.message.content != "Hihi haha") it.sendSimpleMessage("Hihi haha") } }
            }
        ).and(
            interactionMonoWithResume.onErrorResume { interactionMonoWithResume }
        ).and(
            gateway.on(PresenceUpdateEvent::class.java) {
                mono {
                    launch {
                        println("FOR TEST!!!")
                    }
                }
            }

        )

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
