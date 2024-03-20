package fr.univlyon1.m1if.m1if13.users.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorParameter(true)
                .parameterName("format")
                .ignoreAcceptHeader(false)
                .useRegisteredExtensionsOnly(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("html", MediaType.TEXT_HTML);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/login")
                .allowedOrigins("http://localhost", "http://192.168.75.23", "https://192.168.75.23")
                .allowedMethods("POST");

        // Autoriser les origines pour POST /logout
        registry.addMapping("/logout")
                .allowedOrigins("http://localhost", "http://192.168.75.23", "https://192.168.75.23")
                .allowedMethods("POST");

        // Autoriser les origines pour GET /users/{login}
        registry.addMapping("/users/{login}")
                .allowedOrigins("http://localhost", "http://192.168.75.23", "https://192.168.75.23")
                .allowedMethods("GET");
    }
}
