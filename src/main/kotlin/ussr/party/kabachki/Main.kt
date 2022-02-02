package ussr.party.kabachki

import discord4j.core.DiscordClient
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.mono
import ussr.party.kabachki.command.CommandProcessorImpl

const val token = "OTM3Njk3Mjg3NTcwODUzOTM4.Yffgpg.wpW2sM8hWGRCYpEtMgEOWT5As6o" // TODO: hide
private val discordClient = DiscordClient.builder(token).build()

private val commandProcessorImpl = CommandProcessorImpl()

fun main() {
    discordClient.withGateway {
        mono {
            it.on(MessageCreateEvent::class.java)
                .asFlow()
                .collect { launch { commandProcessorImpl.parseCommand(it) } }
        }
    }.block()
}
