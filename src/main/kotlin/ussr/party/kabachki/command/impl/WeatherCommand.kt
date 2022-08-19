package ussr.party.kabachki.command.impl

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import kotlinx.coroutines.reactive.awaitFirst
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.netty.http.client.HttpClient
import ussr.party.kabachki.command.Command
import ussr.party.kabachki.dto.WeatherDTO
import ussr.party.kabachki.extension.getOptionByNameOrNull
import ussr.party.kabachki.extension.getUserMention
import ussr.party.kabachki.extension.replyTo
import java.time.Instant

class WeatherCommand : Command {
    private val apiKey = "508dff1acdabb7268e658e51914a2567"
    private val baseUrl = "https://api.openweathermap.org"
    private val httpClient: HttpClient = HttpClient.create().baseUrl(baseUrl)
    private val objectMapper = ObjectMapper().apply {
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    override fun getName() = "weather"

    override suspend fun executeCommand(event: ChatInputInteractionEvent) {
        val fullResponse = event.getOptionByNameOrNull("full")?.asBoolean()
        val response = httpClient.get()
            .uri("/data/2.5/weather?q=Voronezh&appid=$apiKey&units=metric")
            .responseContent()
            .aggregate()
            .asString()
            .awaitFirst()

        val parsedResponse = objectMapper.readValue(response, WeatherDTO::class.java).also {
            logger.info(it.toString())
        }

        event.run {
            replyTo(
                """
                Hey ${getUserMention()}! 
                Current temp in **${parsedResponse.name}** equal **${parsedResponse.main.temp}℃**
                """.trimIndent()
                    +
                if (fullResponse == true)
                    """
                    
                    Coordinate: latitude = **${parsedResponse.coord?.lat.valueOrAbsent()}**, longitude = **${parsedResponse.coord?.lon.valueOrAbsent()}**
                    Weather info: **${parsedResponse.weather?.first()?.main.valueOrAbsent()}**, **${parsedResponse.weather?.first()?.description.valueOrAbsent()}**
                    Temp: **${parsedResponse.main.temp}℃**, feels like **${parsedResponse.main.feelsLike}℃**, humidity **${parsedResponse.main.humidity}%**
                    Pressure: **${parsedResponse.main.pressure} hPa**
                    Wind speed: **${parsedResponse.wind?.speed.valueOrAbsent()} m/s**
                    Cloudiness: **${parsedResponse.clouds?.all.valueOrAbsent()}%**
                    Visibility: **${parsedResponse.visibility} m**
                    Sunset: ${Instant.ofEpochSecond(parsedResponse.sys?.sunset?.toLong() ?: 0)}
                    Sunrise: ${Instant.ofEpochSecond(parsedResponse.sys?.sunrise?.toLong() ?: 0)}
                    
                    Processing date is 
                    **${parsedResponse.dt}** (Unix date-time, UTC)
                    **${Instant.ofEpochSecond(parsedResponse.dt.toLong())}** (Instant, UTC)
                    **${Instant.ofEpochSecond(parsedResponse.dt.toLong() + parsedResponse.timezone)}** (Instant, Location)
                    """.trimIndent()
                else
                    ""
            )
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)

        fun String?.valueOrAbsent() = this ?: "absent"
        fun Double?.valueOrAbsent() = this?.toString() ?: "absent"
        fun Int?.valueOrAbsent() = this?.toString() ?: "absent"
    }
}