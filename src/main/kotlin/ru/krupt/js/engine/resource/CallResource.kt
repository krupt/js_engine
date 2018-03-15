package ru.krupt.js.engine.resource

import io.swagger.annotations.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.krupt.js.engine.dto.CallFullInfoDto
import ru.krupt.js.engine.service.CallExecutionService
import ru.krupt.js.engine.service.CallStorageService

@RestController
@RequestMapping("/api/v1/call")
class CallResource(val callStorageService: CallStorageService,
                   val callExecutionService: CallExecutionService) {

    companion object {
        @JvmStatic
        private val log: Logger = LoggerFactory.getLogger(CallResource::class.java)
    }

    @ApiOperation("Получить список всех коллов")
    @GetMapping
    fun getCalls() = callStorageService.getAll()

    @ApiOperation("Загрузить JavaScript-файл с кодом колла",
            notes = "Имя файла должно заканчиваться на .js")
    @PostMapping
    fun uploadCall(@RequestParam("jsFile") file: MultipartFile): CallFullInfoDto {
        val fileName = file.originalFilename
        if (fileName == null || !fileName.endsWith(".js")) {
            throw IllegalArgumentException("File must be a JavaScript")
        }
        val callName = fileName.substring(0, fileName.length - 3)
        log.info("Loading call '{}' from file '{}'", callName, fileName)

        return callStorageService.parseInputStreamAndSaveCall(file.inputStream, callName)
    }

    @ApiOperation("Получить полную информацию по коллу")
    @GetMapping("{callName}")
    fun getCall(@PathVariable("callName") callName: String) =
            callStorageService.getCall(callName)

    @ApiOperation("Удалить колл")
    @DeleteMapping("{callName}")
    fun deleteCall(@PathVariable("callName") callName: String) {
        callStorageService.deleteCall(callName)
    }

    @ApiOperation("Получить полную информацию по коллу")
    @GetMapping("id/{callId}")
    fun getCallById(@PathVariable("callId") callId: Long) =
            callStorageService.getCallById(callId)

    @ApiOperation("Получить входные и выходные параметры колла")
    @GetMapping("{callName}/invocationInfo")
    fun getCallInvocationInfo(@PathVariable("callName") callName: String) =
            callStorageService.getCallInvocationInfo(callName)

    @ApiOperation("Выполнить колл")
    @PostMapping("{callName}")
    @ApiResponses(ApiResponse(code = 200, message = "Значения выходных параметров колла"))
    fun performCall(@PathVariable("callName") callName: String,

                    @ApiParam("Значения входных параметров колла", examples = Example(
                            ExampleProperty("""{
    "SOME_NULLABLE_VARIABLE": null,
    "SOME_BOOLEAN_VARIABLE": true,
    "SOME_INTEGER_VARIABLE": 123,
    "SOME_DECIMAL_VARIABLE": 123.21,
    "SOME_TEXT_VARIABLE": "Hello",
    "SOME_DATE_VARIABLE": "2018-03-08",
    "SOME_DATE_TIME_VARIABLE": "2018-02-23T12:13:14"
}""")))
                    @RequestBody(required = false) inputs: Map<String, Any?>?): Map<String, Any?> =
            callExecutionService.call(callName, inputs ?: emptyMap())
}
