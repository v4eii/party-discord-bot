package ussr.party.kabachki.exception


class MemberIsNotPresentException : RuntimeException()

class VoiceStateIsNotExistException : RuntimeException()

class LaunchException(message: String) : RuntimeException(message)

class RegisterCommandException(message: String) : RuntimeException(message)

class MessageChannelNotFoundException : RuntimeException("Message channel not found!")