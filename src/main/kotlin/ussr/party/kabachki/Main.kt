package ussr.party.kabachki

import discord4j.core.DiscordClient
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.mono
import ussr.party.kabachki.command.CommandProcessorImpl

private val commandProcessorImpl = CommandProcessorImpl()
val configHolder = mutableMapOf<String, Any>()

fun main(args: Array<String>) {
    val discordClient = DiscordClient.builder(args[0]).build()
    configHolder["token"] = args[0]

    discordClient.withGateway {
        mono {
            it.on(MessageCreateEvent::class.java)
                .asFlow()
                .collect { launch { commandProcessorImpl.parseCommand(it) } }
        }
    }.block()
}
