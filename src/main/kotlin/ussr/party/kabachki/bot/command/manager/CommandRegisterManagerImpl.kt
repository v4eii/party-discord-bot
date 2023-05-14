package ussr.party.kabachki.bot.command.manager

import discord4j.common.JacksonResources
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.rest.RestClient
import org.slf4j.LoggerFactory
import ussr.party.kabachki.exception.RegisterCommandException
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.streams.toList

class CommandRegisterManagerImpl(private val restClient: RestClient) : CommandRegisterManager {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun registerCommands(requests: List<ApplicationCommandRequest>, fileCommands: List<String>) {
        if (requests.isEmpty().not()) {
            val appId = restClient.applicationId.block() ?: throw RegisterCommandException("appId is null")
            val applicationService = restClient.applicationService
            val existedCommands = applicationService.getGlobalApplicationCommands(appId).collectList().block()
                ?: emptyList()

            requests.filter { it.name() !in existedCommands.map { request -> request.name() } }
                .forEach {
                    applicationService.createGlobalApplicationCommand(appId, it)
                        .doOnNext { cmd -> logger.info("Command '${cmd.name()}' successfully registered") }
                        .subscribe()
                }
        } else if (fileCommands.isEmpty().not()) {
            registerCommands(requests = readFilesAndMapToCommandRequest(fileCommands))
        }
    }

    override fun deleteCommands(commands: List<ApplicationCommandRequest>) {
        if (commands.isEmpty().not()) {
            val appId = restClient.applicationId.block() ?: throw RegisterCommandException("appId is null")
            val applicationService = restClient.applicationService

            applicationService.getGlobalApplicationCommands(appId)
                .filter { existsCommand -> existsCommand.name() in commands.map { it.name() } }
                .map { applicationService.deleteGlobalApplicationCommand(appId, it.id().asLong()) }
                .doOnComplete { logger.info("Commands deleted") }
                .subscribe()
        }

        logger.info("Command list is empty")
    }

    companion object {
        private const val commandsFolderName = "commands/"
        private val jackson = JacksonResources.create()

        private fun readFilesAndMapToCommandRequest(fileNames: List<String>): List<ApplicationCommandRequest> =
            fileNames.mapNotNull { getResourceFileAsString(commandsFolderName + it) }
                .map { jackson.objectMapper.readValue(it, ApplicationCommandRequest::class.java) }

        private fun getResourceFileAsString(fileName: String): String? {
            ClassLoader.getSystemClassLoader()
                .getResourceAsStream(fileName)
                .use { inputStream ->
                    if (inputStream == null) {
                        return null
                    }
                    BufferedReader(InputStreamReader(inputStream)).use {
                        return it.lines().toList().joinToString(separator = System.lineSeparator())
                    }
                }
        }
    }
}