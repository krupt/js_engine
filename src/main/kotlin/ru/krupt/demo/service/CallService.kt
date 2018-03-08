package ru.krupt.demo.service

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.krupt.demo.domain.CallEntity
import ru.krupt.demo.dto.CallFullInfoDto
import ru.krupt.demo.dto.CallMetaData
import ru.krupt.demo.errors.CallNotFoundException
import ru.krupt.demo.repository.CallRepository
import ru.krupt.demo.util.unwrap
import java.io.IOException
import java.io.InputStream

@Service
class CallService(
        val callRepository: CallRepository,
        objectMapper: ObjectMapper) {
    private final val javaScriptObjectMapper: ObjectMapper = objectMapper.copy()

    @Autowired
    private lateinit var self: CallService

    init {
        javaScriptObjectMapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
        javaScriptObjectMapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
        javaScriptObjectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS)
    }

    fun getCallInvocationInfo(callName: String) =
            callRepository.findInvocationInfoByNameIgnoreCase(callName)?.getInvocationInfo()
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

        return CallFullInfoDto.fromEntity(self.createOrUpdateCall(callName, callMetaData, callBody))
    }

    // Do it in one transaction to avoid detach object (and executing two select queries)
    @Transactional
//    @Suppress("RedundantVisibilityModifier")
    fun createOrUpdateCall(callName: String,
                           callMetaData: CallMetaData,
                           callBody: String): CallEntity {
        var callEntity: CallEntity? = callRepository.findOneByNameIgnoreCase(callName)
        callEntity = if (callEntity != null) {
            callEntity.copy(description = callMetaData.description,
                    invocationInfo = callMetaData.invocationInfo,
                    body = callBody)
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
}
