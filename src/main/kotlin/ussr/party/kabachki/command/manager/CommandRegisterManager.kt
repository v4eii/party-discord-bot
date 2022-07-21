package ussr.party.kabachki.command.manager

import discord4j.discordjson.json.ApplicationCommandRequest

interface CommandRegisterManager {
    fun registerCommands(
        requests: List<ApplicationCommandRequest> = emptyList(),
        fileCommands: List<String> = emptyList()
    )

    fun deleteCommands(commands: List<ApplicationCommandRequest> = emptyList())
}