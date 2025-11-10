package com.sasps.hotelbooking.config;

import java.util.List;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hotelBookingOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        Info info = new Info()
                .title("Hotel Booking API - Monolithic Architecture")
                .version("1.0.0")
                .description("REST API for hotel room booking system.");
        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}
