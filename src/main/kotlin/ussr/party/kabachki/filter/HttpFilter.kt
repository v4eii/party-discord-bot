package ussr.party.kabachki.filter

import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class HttpFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        exchange.response.apply {
            if (true == statusCode?.is4xxClientError || true == statusCode?.is5xxServerError) {
                this.headers.add("X-Generator", "kabachok-discord-bot")
            }
        }

        return chain.filter(exchange)
    }
}