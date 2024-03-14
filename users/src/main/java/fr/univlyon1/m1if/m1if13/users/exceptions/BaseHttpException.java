package fr.univlyon1.m1if.m1if13.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class BaseHttpException extends RuntimeException {
    private final HttpStatus status;

    public BaseHttpException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatusCode getStatus() {
        return status;
    }
}
