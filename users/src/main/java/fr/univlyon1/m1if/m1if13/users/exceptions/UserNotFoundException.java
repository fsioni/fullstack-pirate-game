package fr.univlyon1.m1if.m1if13.users.exceptions;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseHttpException{
    public UserNotFoundException() {
        super("User not found", HttpStatus.NOT_FOUND);
    }
}
