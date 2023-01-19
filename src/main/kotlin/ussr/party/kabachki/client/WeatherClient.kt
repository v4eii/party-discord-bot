package ussr.party.kabachki.client

import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import ussr.party.kabachki.dto.WeatherDTO

class WeatherClient(
    private val webClient: WebClient,
    private val apiKey: String
) {

    suspend fun getWeatherInfo(): WeatherDTO =
        webClient.get()
            .uri {
                it.path("/data/2.5/weather")
                    .queryParam("q", "Voronezh")
                    .queryParam("appid", apiKey)
                    .queryParam("units", "metric")
                    .build()
            }
            .retrieve()
            .bodyToMono<WeatherDTO>()
            .awaitFirst()

}