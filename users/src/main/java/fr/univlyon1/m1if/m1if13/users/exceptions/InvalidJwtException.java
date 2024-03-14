package fr.univlyon1.m1if.m1if13.users.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidJwtException extends BaseHttpException {
    public InvalidJwtException() {
        super("Invalid JWT", HttpStatus.UNAUTHORIZED);
    }
}
