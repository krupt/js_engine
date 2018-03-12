package ru.krupt.js.engine.resource

import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.krupt.js.engine.dto.CallFullInfoDto
import ru.krupt.js.engine.service.CallExecutionService
import ru.krupt.js.engine.service.CallStorageService

@RestController
@RequestMapping("/api/v1/call")
class CallResource(val callStorageService: CallStorageService,
                   val callExecutionService: CallExecutionService) {

    @PostMapping
    fun uploadCall(@RequestParam("jsFile") file: MultipartFile): CallFullInfoDto {
        val callName = file.originalFilename
        if (callName == null || !callName.endsWith(".js")) {
            throw IllegalArgumentException("File must be a JavaScript")
        }

        return callStorageService.parseInputStreamAndSaveCall(file.inputStream,
                callName.substring(0, callName.length - 3))
    }

    @GetMapping("{callName}")
    fun getCall(@PathVariable("callName") callName: String) =
            callStorageService.getCall(callName)

    @GetMapping("id/{callId}")
    fun getCallById(@PathVariable("callId") callId: Long) =
            callStorageService.getCallById(callId)

    @GetMapping("{callName}/invocationInfo")
    fun getCallInvocationInfo(@PathVariable("callName") callName: String) =
            callStorageService.getCallInvocationInfo(callName)

    @PostMapping("{callName}")
    fun performCall(@PathVariable("callName") callName: String,
                    @RequestBody(required = false) inputs: Map<String, Any?>?): Map<String, Any?> =
            callExecutionService.call(callName, inputs ?: emptyMap())
}
