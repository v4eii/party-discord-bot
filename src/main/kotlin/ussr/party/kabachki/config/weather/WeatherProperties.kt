package ussr.party.kabachki.config.weather

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("client.weather")
data class WeatherProperties(
    val baseUrl: String,
    val apiKey: String
)