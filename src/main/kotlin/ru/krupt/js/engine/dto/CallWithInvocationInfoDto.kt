package ru.krupt.js.engine.dto

import ru.krupt.js.engine.domain.CallEntityWithoutBody
import ru.krupt.js.engine.variable.InvocationInfo

data class CallWithoutBodyDto(
        val id: Long,
        val name: String,
        val description: String,
        val invocationInfo: InvocationInfo,
        val version: Int) {

    companion object {

        fun fromEntity(callEntity: CallEntityWithoutBody) = CallWithoutBodyDto(callEntity.id,
                callEntity.name,
                callEntity.description,
                callEntity.invocationInfo,
                callEntity.version)
    }
}
