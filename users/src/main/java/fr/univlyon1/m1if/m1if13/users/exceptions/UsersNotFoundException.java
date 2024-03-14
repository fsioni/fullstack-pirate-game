package fr.univlyon1.m1if.m1if13.users.exceptions;

import org.springframework.http.HttpStatus;

public class UsersNotFoundException extends BaseHttpException{
    public UsersNotFoundException() {
        super("There is no users", HttpStatus.NOT_FOUND);
    }
}
