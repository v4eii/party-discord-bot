package ussr.party.kabachki.bot.command.impl

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.springframework.stereotype.Component
import ussr.party.kabachki.bot.command.Command
import ussr.party.kabachki.bot.extension.getOptionByNameOrNull
import ussr.party.kabachki.bot.extension.getUserMention
import ussr.party.kabachki.bot.extension.replyTo
import ussr.party.kabachki.client.WeatherClient
import java.time.Instant

@Component
class WeatherCommand(
    private val weatherClient: WeatherClient
) : Command {

    override fun getName() = "weather"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        val isFullResponse = event.getOptionByNameOrNull("full")?.asBoolean()
        val weather = weatherClient.getWeatherInfo()

        weather.run {
            event.replyTo(
                """
                Hey ${event.getUserMention()}! 
                Current temp in **${name}** equal **${main.temp}℃**
                """.trimIndent()
                        +
                        if (isFullResponse == true)
                            """
                    
                    Coordinate: latitude = **${coord?.lat.valueOrAbsent()}**, longitude = **${coord?.lon.valueOrAbsent()}**
                    Weather info: **${weather.weather?.first()?.main.valueOrAbsent()}**, **${weather.weather?.first()?.description.valueOrAbsent()}**
                    Temp: **${main.temp}℃**, feels like **${main.feelsLike}℃**, humidity **${main.humidity}%**
                    Pressure: **${main.pressure} hPa**
                    Wind speed: **${wind?.speed.valueOrAbsent()} m/s**
                    Cloudiness: **${clouds?.all.valueOrAbsent()}%**
                    Visibility: **${visibility} m**
                    Sunset: **${Instant.ofEpochSecond((sys?.sunset?.toLong() ?: 0) + timezone)}** (Instant, with timezone)
                    Sunrise: **${Instant.ofEpochSecond((sys?.sunrise?.toLong() ?: 0) + timezone)}** (Instant, with timezone)
                    
                    Processing date is 
                    **${dt}** (Unix date-time, UTC)
                    **${Instant.ofEpochSecond(dt.toLong())}** (Instant, UTC)
                    **${Instant.ofEpochSecond(dt.toLong() + timezone)}** (Instant, with timezone)
                    """.trimIndent()
                        else ""
            )
        }
    }

    fun String?.valueOrAbsent() = this ?: "absent"
    fun Double?.valueOrAbsent() = this?.toString() ?: "absent"
    fun Int?.valueOrAbsent() = this?.toString() ?: "absent"

}