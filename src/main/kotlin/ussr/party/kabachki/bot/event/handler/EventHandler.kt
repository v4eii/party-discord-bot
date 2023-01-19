package ussr.party.kabachki.bot.event.handler

fun interface EventHandler<T> {
    suspend fun handle(event: T)
}