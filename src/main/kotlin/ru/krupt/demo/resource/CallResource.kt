package ru.krupt.demo.resource

import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.krupt.demo.dto.CallFullInfoDto
import ru.krupt.demo.service.CallService

@RestController
@RequestMapping("/api/v1/call")
class CallResource(val callService: CallService) {

    @PostMapping
    fun uploadCall(@RequestParam("jsFile") file: MultipartFile): CallFullInfoDto {
        val callName = file.originalFilename
        if (callName == null || !callName.endsWith(".js")) {
            throw IllegalArgumentException("File must be a JavaScript")
        }

        return callService.parseInputStreamAndSaveCall(file.inputStream,
                callName.substring(0, callName.length - 3))
    }

    @GetMapping("{callName}")
    fun getCall(@PathVariable("callName") callName: String) =
            callService.getCall(callName)

    @GetMapping("id/{callId}")
    fun getCallById(@PathVariable("callId") callId: Long) =
            callService.getCallById(callId)

    @GetMapping("{callName}/invocationInfo")
    fun getCallInvocationInfo(@PathVariable("callName") callName: String) =
            callService.getCallInvocationInfo(callName)

    @PostMapping("{callName}")
    fun performCall(@PathVariable("callName") callName: String,
                    @RequestBody(required = false) inputs: Map<String, Any?>?): Map<String, Any?> =
            callService.call(callName, inputs ?: emptyMap())
}
