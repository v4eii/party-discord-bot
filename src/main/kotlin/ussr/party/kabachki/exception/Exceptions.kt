package ussr.party.kabachki.exception

open class DiscordElementNotExistException(message: String = "") : RuntimeException(message)

class MemberIsNotPresentException : DiscordElementNotExistException()

class VoiceStateIsNotExistException : DiscordElementNotExistException()

class VoiceChannelIsNotExistException : DiscordElementNotExistException()

class MessageChannelNotFoundException : DiscordElementNotExistException("Message channel not found!")

class LaunchException(message: String) : RuntimeException(message)

class OptionNotFoundException(message: String) : RuntimeException(message)

class RegisterCommandException(message: String) : RuntimeException(message)
