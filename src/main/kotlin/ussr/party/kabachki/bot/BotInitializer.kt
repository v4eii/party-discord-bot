package ussr.party.kabachki.bot

import discord4j.common.retry.ReconnectOptions
import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import discord4j.gateway.ShardInfo
import discord4j.gateway.intent.IntentSet
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import ussr.party.kabachki.bot.command.manager.CommandRegisterManagerImpl
import ussr.party.kabachki.bot.event.EventCollector
import ussr.party.kabachki.bot.event.handler.impl.ChatInputInteractionEventHandler
import ussr.party.kabachki.bot.event.handler.impl.MessageCreateEventHandler
import ussr.party.kabachki.bot.event.handler.impl.PresenceUpdateEventHandler
import ussr.party.kabachki.bot.event.handler.impl.ReadyEventHandler
import ussr.party.kabachki.bot.event.handler.impl.SendMessageEventHandler
import ussr.party.kabachki.bot.event.handler.impl.VoiceStateUpdateEventHandler
import ussr.party.kabachki.config.bot.BotElements
import ussr.party.kabachki.config.bot.BotProperties
import ussr.party.kabachki.extension.getLogger
import java.time.Duration

@Component
@EnableConfigurationProperties(BotProperties::class)
class BotInitializer(
    private val botProperties: BotProperties,
    private val chatInputInteractionEventHandler: ChatInputInteractionEventHandler,
    private val readyEventHandler: ReadyEventHandler,
    private val messageCreateEventHandler: MessageCreateEventHandler,
    private val presenceUpdateEventHandler: PresenceUpdateEventHandler,
    private val voiceStateUpdateEventHandler: VoiceStateUpdateEventHandler,
    private val sendMessageEventHandler: SendMessageEventHandler,
    private val botElements: BotElements
) {
    val logger: Logger = getLogger<BotInitializer>()

    @EventListener(ApplicationReadyEvent::class)
    fun startBot() {
        DiscordClient.builder(botProperties.token)
            .build()
            .gateway()
            .setEnabledIntents(IntentSet.all())
            .setReconnectOptions(
                ReconnectOptions.builder()
                    .setFirstBackoff(Duration.ofSeconds(2))
                    .setMaxBackoffInterval(Duration.ofSeconds(15L))
                    .setMaxRetries(Long.MAX_VALUE)
                    .build()
            )
            .withGateway { gateway ->
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
                                "skip.json",
                                "move-cumrade.json",
                                "weather.json"
                            )
                        )
                }
                    .and(
                        mono {
                            botElements.apply {
                                shardInfo = ShardInfo.create(
                                    gateway.gatewayClientGroup.computeShardIndex(gateway.guilds.awaitFirst().id),
                                    gateway.gatewayClientGroup.shardCount
                                )
                                gatewayDiscordClient = gateway
                            }

                        }
                    )
                    .and(EventCollector.collectOneEvent(gateway, readyEventHandler))
                    .and(EventCollector.collectEvents(gateway, messageCreateEventHandler))
                    .and(EventCollector.collectEvents(gateway, chatInputInteractionEventHandler))
                    .and(EventCollector.collectEvents(gateway, presenceUpdateEventHandler))
                    .and(EventCollector.collectEvents(gateway, voiceStateUpdateEventHandler))
                    .and(EventCollector.collectEvents(gateway, sendMessageEventHandler))
                    .withDebug(gateway)
            }.block()
    }

    fun Mono<*>.withDebug(gateway: GatewayDiscordClient) =
        and(gateway.on(Event::class.java) {
            mono {
                launch {
                    logger.debug(it.toString())
                }
            }
        })
}