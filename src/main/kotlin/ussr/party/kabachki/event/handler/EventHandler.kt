package ussr.party.kabachki.event.handler

fun interface EventHandler<T> {
    suspend fun handle(event: T)
}