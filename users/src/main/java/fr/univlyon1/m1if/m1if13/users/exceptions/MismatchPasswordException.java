package fr.univlyon1.m1if.m1if13.users.exceptions;

import org.springframework.http.HttpStatus;

public class MismatchPasswordException extends BaseHttpException {
    public MismatchPasswordException() {
        super("The provided password does not match the user's password.", HttpStatus.UNAUTHORIZED);
    }
}
