package ussr.party.kabachki.handler

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ServerWebExchange
import ussr.party.kabachki.model.BaseErrorDTO

@ControllerAdvice
class HttpExceptionHandler {

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(exchange: ServerWebExchange, ex: Exception) = ResponseEntity.internalServerError().body(
        BaseErrorDTO(
            errorCode = "unknown",
            errorMessage = ex.message
        )
    )

}