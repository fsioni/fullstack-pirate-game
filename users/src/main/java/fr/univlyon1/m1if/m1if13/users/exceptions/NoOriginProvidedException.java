package fr.univlyon1.m1if.m1if13.users.exceptions;

import org.springframework.http.HttpStatus;

public class NoOriginProvidedException extends BaseHttpException {
    public NoOriginProvidedException() {
        super("No Origin provided", HttpStatus.BAD_REQUEST);
    }
}
