package com.crud.market_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI marketOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Market API")
                        .description("API for managing products in the market")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Hass")
                                .email("heitor29_@hotmail.com")
                                .url("https://github.com/hvmello"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}