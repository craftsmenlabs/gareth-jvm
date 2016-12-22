package org.craftsmenlabs.gareth.execution

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors.regex
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
@Profile("!Test")
open class SwaggerConfig {

    @Bean
    open fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any()).paths(regex("^\\/gareth\\/v1.*$")).build().apiInfo(apiInfo())
    }

    private fun apiInfo(): ApiInfo {
        val info = ApiInfoBuilder()
        info.description("This is the public REST api to handle glue line code execution for a single project/client")
        info.title("Gareth execution REST API")
        return info.build()
    }

}