package ru.krupt.js.engine.service

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.krupt.js.engine.domain.CallEntity
import ru.krupt.js.engine.dto.CallFullInfoDto
import ru.krupt.js.engine.dto.CallMetaData
import ru.krupt.js.engine.dto.CallWithoutBodyDto
import ru.krupt.js.engine.errors.CallNotFoundException
import ru.krupt.js.engine.repository.CallRepository
import ru.krupt.js.engine.util.unwrap
import java.io.IOException
import java.io.InputStream
import java.util.stream.Collectors
import javax.validation.Validator

@Service
class CallStorageService(
        val callRepository: CallRepository,
        val validator: Validator,
        objectMapper: ObjectMapper) {
    private final val javaScriptObjectMapper: ObjectMapper = objectMapper.copy()

    @Autowired
    private lateinit var self: CallStorageService

    init {
        javaScriptObjectMapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
        javaScriptObjectMapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
        javaScriptObjectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS)
    }

    fun getCallInvocationInfo(callName: String) =
            callRepository.findOneWithoutBodyByNameIgnoreCase(callName)?.invocationInfo
                    ?: throw CallNotFoundException(callName)

    fun parseInputStreamAndSaveCall(inputStream: InputStream, callName: String): CallFullInfoDto {
        val callBody: String = try {
            inputStream.bufferedReader()
                    .use { it.readText() }
        } catch (e: IOException) {
            throw RuntimeException("Error occurred when reading call's body", e)
        }
        val metaDataRegex = Regex("""meta\s*=\s*\{""")
        val matchResult = metaDataRegex.find(callBody)
        val metaInfoStartIndex = matchResult?.groups?.get(0)?.range?.endInclusive
                ?: throw IllegalArgumentException("Couldn't find meta")

        val callMetaData: CallMetaData = try {
            javaScriptObjectMapper.readValue(callBody.substring(metaInfoStartIndex),
                    CallMetaData::class.java)
        } catch (e: IOException) {
            throw IllegalArgumentException("Invalid format of call's meta information", e)
        }

        val errors = validator.validate(callMetaData)

        if (!errors.isEmpty()) {
            throw IllegalArgumentException(errors.joinToString(",\n",
                    "Call's meta validation failed: \n") { "${it.propertyPath} ${it.message}" })
        }

        return CallFullInfoDto.fromEntity(self.createOrUpdateCall(callName, callMetaData, callBody))
    }

    // Do it in one transaction to avoid detach object (and executing two select queries)
    @Transactional
    fun createOrUpdateCall(callName: String,
                           callMetaData: CallMetaData,
                           callBody: String): CallEntity {
        var callEntity = callRepository.findOneByNameIgnoreCase(callName)
        callEntity = if (callEntity != null) {
            val copy = callEntity.copy(description = callMetaData.description,
                    invocationInfo = callMetaData.invocationInfo,
                    body = callBody)
            copy.lastModifiedTime = callEntity.lastModifiedTime
            // return copy
            copy
        } else {
            CallEntity(name = callName, description = callMetaData.description,
                    invocationInfo = callMetaData.invocationInfo,
                    body = callBody)
        }
        return callRepository.save(callEntity)
    }

    fun getCall(callName: String) = CallFullInfoDto.fromEntity(
            callRepository.findOneByNameIgnoreCase(callName)
                    ?: throw CallNotFoundException(callName))

    fun getCallById(callId: Long) = CallFullInfoDto.fromEntity(
            callRepository.findById(callId).unwrap()
                    ?: throw CallNotFoundException(callId.toString()))

    fun deleteCall(callName: String) {
        if (callRepository.deleteByNameIgnoreCase(callName) == 0) {
            throw CallNotFoundException(callName)
        }
    }

    fun getAll(): List<CallWithoutBodyDto> =
            callRepository.findAllWithoutBodyBy()
                    .stream()
                    .map(CallWithoutBodyDto.Companion::fromEntity)
                    .collect(Collectors.toList())
}
