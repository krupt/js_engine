package ru.krupt.demo.service

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import ru.krupt.demo.TestJacksonConfiguration
import ru.krupt.demo.domain.CallEntity
import ru.krupt.demo.repository.CallRepository
import ru.krupt.demo.variable.DataType
import ru.krupt.demo.variable.InvocationInfo
import ru.krupt.demo.variable.Variable

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [TestJacksonConfiguration::class, CallService::class])
class CallServiceTest {

    @Autowired
    lateinit var callService: CallService

    @MockBean
    lateinit var callRepository: CallRepository

    @Test
    fun testSuccessfulSaveNewCallWithJsStyleMeta() {
        val inputStream = Mockito.spy(
                this.javaClass.getResourceAsStream("/call/simple_case.js"))

        BDDMockito.given(callRepository.save(any(CallEntity::class.java)))
                .will({ (it.getArgument(0) as CallEntity).copy(id = 1) })

        val (id, name, description, invocationInfo, body, version) =
                callService.parseInputStreamAndSaveCall(inputStream, "simple_case")

        assertEquals(1, id)
        assertEquals("simple_case", name)
        assertEquals("Этот колл проверяет существование клиента", description)

        val expectedInvocationInfo = InvocationInfo(
                setOf(Variable(name = "CLIENT_ID", type = DataType.TEXT)),
                setOf(Variable(name = "EXISTS", type = DataType.BOOLEAN)))
        assertEquals(expectedInvocationInfo, invocationInfo)

        val expectedBody = this.javaClass.getResourceAsStream("/call/simple_case.js")
                .bufferedReader().use { it.readText() }
        assertEquals(expectedBody, body)
        assertEquals(0, version)

        Mockito.verify(inputStream).close()
    }
}
