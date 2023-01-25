package ussr.party.kabachki.extension

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ussr.party.kabachki.consts.PartySystem
import ussr.party.kabachki.model.ChannelMessageDTO

// Snowflake{937689943814328320} guild id
// Snowflake{937785108696551546} channelId

inline fun <reified T> getLogger(): Logger = LoggerFactory.getLogger(T::class.java)

fun String.discordMention() = "<@$this>"

fun ChannelMessageDTO.PartySystem.toServerPartySystem() = PartySystem.valueOf(this.value)