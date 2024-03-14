package fr.univlyon1.m1if.m1if13.users.exceptions;

import org.springframework.http.HttpStatus;

public class LoginAlreadyExistsException extends BaseHttpException {
    public LoginAlreadyExistsException() {
        super("Login is already used", HttpStatus.CONFLICT);
    }
}
