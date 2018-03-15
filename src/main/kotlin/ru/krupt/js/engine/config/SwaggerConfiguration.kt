package ru.krupt.js.engine.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class SwaggerConfiguration {

    @Bean
    fun swaggerApi(): Docket = Docket(DocumentationType.SWAGGER_2)
            .apiInfo(ApiInfo("JavaScript Engine",
                    "Движок для хранения и запуска JavaScript функций", "1.0",
                    "",
                    Contact("krupt", "https://github.com/krupt",
                            "krupt25@gmail.com"), "", "", emptyList()
            ))
            .select()
            .apis(RequestHandlerSelectors.basePackage("ru.krupt.js.engine.resource"))
            .paths(PathSelectors.any())
            .build()
}
