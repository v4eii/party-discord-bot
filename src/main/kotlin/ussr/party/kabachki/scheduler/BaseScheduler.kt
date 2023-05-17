package ussr.party.kabachki.scheduler

import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ussr.party.kabachki.bot.event.SendMessageEvent
import ussr.party.kabachki.config.bot.BotElements
import ussr.party.kabachki.model.ChannelMessageDTO

@Component
@EnableScheduling
class BaseScheduler(
    private val botElements: BotElements
) {

    @Scheduled(cron = "* * * * * TUE")
    fun furryScheduler() {
        botElements.run {
            gatewayDiscordClient
                .eventDispatcher
                .publish(
                    SendMessageEvent(
                        gatewayDiscordClient,
                        shardInfo,
                        ChannelMessageDTO(
                            content = "https://cdn.discordapp.com/attachments/937785108696551546/1108441706178809856/qwe.gif"
                        )
                    )
                )
        }
    }

}