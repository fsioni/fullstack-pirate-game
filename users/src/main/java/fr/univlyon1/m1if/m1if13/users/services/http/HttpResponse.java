package fr.univlyon1.m1if.m1if13.users.services.http;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public class HttpResponse extends ResponseEntity<Map<String, Object>> {
    public HttpResponse(ResponseEntity<Map<String, Object>> responseEntity) {
        super(responseEntity.getBody(), responseEntity.getHeaders(),responseEntity.getStatusCode());
    }
}