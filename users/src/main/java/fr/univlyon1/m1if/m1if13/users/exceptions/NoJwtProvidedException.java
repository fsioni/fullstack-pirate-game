package fr.univlyon1.m1if.m1if13.users.exceptions;

import org.springframework.http.HttpStatus;

public class NoJwtProvidedException extends BaseHttpException {
    public NoJwtProvidedException() {
        super("No JWT provided", HttpStatus.BAD_REQUEST);
    }
}
