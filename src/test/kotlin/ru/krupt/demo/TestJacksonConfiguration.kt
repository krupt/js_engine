package ru.krupt.demo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestJacksonConfiguration {

    @Bean
    fun objectMapper() = ObjectMapper().registerKotlinModule()
}
