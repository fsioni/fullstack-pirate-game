package fr.univlyon1.m1if.m1if13.users.services.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseBuilder {
    private final Map<String, Object> response = new HashMap<>();
    private ResponseEntity.BodyBuilder bodyBuilder;

    public HttpResponseBuilder message(HttpMessage message) {
        this.response.put("message", message.toString());
        return this;
    }

    public HttpResponseBuilder data(String key, Object value) {
        this.response.put(key, value);
        return this;
    }

    public HttpResponseBuilder status(HttpStatus status) {
        this.bodyBuilder = ResponseEntity.status(status);
        return this;
    }

    public HttpResponseBuilder header(HttpHeader header, String value) {
        bodyBuilder.header(header.toString(), value);
        return this;
    }

    public HttpResponse build() {
        ResponseEntity<Map<String, Object>> responseEntity = bodyBuilder.body(response);
        return new HttpResponse(responseEntity);
    }
}