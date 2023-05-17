package ussr.party.kabachki.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import ussr.party.kabachki.consts.PartySystem

@ConstructorBinding
@ConfigurationProperties("users")
data class UserDataProperties(
    val identificators: Map<String, Map<PartySystem, String>>
) {
    fun getUserSystemsIds(username: String): Map<PartySystem, String> = identificators[username] ?: emptyMap()
    fun getUserSystemId(username: String, system: PartySystem = PartySystem.DISCORD) =
        getUserSystemsIds(username)[system]

    fun convertUserSystemId(
        id: String,
        systemFrom: PartySystem = PartySystem.VK,
        systemTo: PartySystem = PartySystem.DISCORD
    ): String = identificators.values.find { it[systemFrom] == id }!![systemTo] ?: throw RuntimeException()
}