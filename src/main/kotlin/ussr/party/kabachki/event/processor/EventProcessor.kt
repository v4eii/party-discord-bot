package ussr.party.kabachki.event.processor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

fun interface EventProcessor<T> {
    fun process(event: T, scope: CoroutineScope): Job
}