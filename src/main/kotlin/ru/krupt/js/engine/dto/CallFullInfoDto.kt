package ru.krupt.js.engine.dto

import io.swagger.annotations.ApiModel
import ru.krupt.js.engine.domain.CallEntity
import ru.krupt.js.engine.variable.InvocationInfo

@ApiModel(description = "Полная информация по коллу")
data class CallFullInfoDto(
        val id: Long,
        val name: String,
        val description: String,
        val invocationInfo: InvocationInfo,
        val body: String,
        val version: Int) {

    companion object {

        fun fromEntity(callEntity: CallEntity) = CallFullInfoDto(callEntity.id ?: 0,
                callEntity.name,
                callEntity.description,
                callEntity.invocationInfo,
                callEntity.body,
                callEntity.version)
    }
}
