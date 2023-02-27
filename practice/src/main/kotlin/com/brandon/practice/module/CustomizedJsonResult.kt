package com.brandon.practice.module

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonValue
import org.springframework.http.HttpStatus


@JsonInclude(JsonInclude.Include.NON_NULL)
class CustomizedJsonResult(open val status: Status,
                           open val error: String? = null,
                           open val message: String? = null,
                           open val contents: Any? = null) {

    data class Status(@JsonValue val code: Int) {
        companion object {
            @JvmField
            val OK = Status(HttpStatus.OK.value())

            @JvmField
            val BAD_REQUEST = Status(HttpStatus.BAD_REQUEST.value())

            @JvmField
            val NOT_FOUND = Status(HttpStatus.NOT_FOUND.value())

            @JvmField
            val INTERNAL_ERROR = Status(HttpStatus.INTERNAL_SERVER_ERROR.value())

            @JvmField
            val EXIST_DATA = Status(900)

            @JvmField
            val DATA_NOT_FOUND = Status(901)
        }
    }

    companion object {
        fun ok(contents: Any? = null) =
            CustomizedJsonResult(Status.OK, null, HttpStatus.OK.reasonPhrase, contents)

        fun okButWarn(message: String? = null) =
            CustomizedJsonResult(Status.OK, null, message, null)

        fun badRequest(message: String? = null) =
            CustomizedJsonResult(Status.BAD_REQUEST, HttpStatus.BAD_REQUEST.reasonPhrase, message)

        fun internalError(message: String? = null) =
            CustomizedJsonResult(Status.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase, message)

        fun notFound(message: String? = null) =
            CustomizedJsonResult(Status.NOT_FOUND, HttpStatus.NOT_FOUND.reasonPhrase, message)

        fun existData(message: String? = null) =
            CustomizedJsonResult(Status.EXIST_DATA, "EXIST DATA", message)

        fun dataNotFound(message: String? = null) =
            CustomizedJsonResult(Status.DATA_NOT_FOUND, "DATA NOT FOUND", message)
    }
}
