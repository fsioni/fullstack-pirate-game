package fr.univlyon1.m1if.m1if13.users.controllers;

import fr.univlyon1.m1if.m1if13.users.dao.UserDao;
import fr.univlyon1.m1if.m1if13.users.dto.CreateUserDto;
import fr.univlyon1.m1if.m1if13.users.models.User;
import fr.univlyon1.m1if.m1if13.users.services.http.HttpMessage;
import fr.univlyon1.m1if.m1if13.users.services.http.HttpResponse;
import fr.univlyon1.m1if.m1if13.users.services.http.HttpResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

/**
 * This class represents a controller for CRUD operations on users.
 */
@RestController()
@RequestMapping("/users")
public class UsersCrudController {
    UserDao userDao;

    @Autowired
    public UsersCrudController(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Retrieves all users.
     *
     * @return a ResponseEntity containing a message, a status code and the users
     */
    @GetMapping()
    public HttpResponse getUsers() {
        Set<String> users = userDao.getAll();
        if (users.isEmpty()) {
            return new HttpResponseBuilder()
                    .message(HttpMessage.NO_USERS_FOUND)
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return new HttpResponseBuilder()
                .message(HttpMessage.USERS_FOUND)
                .data("users", users)
                .status(HttpStatus.OK)
                .build();
    }

    /**
     * Retrieves a user by their login.
     *
     * @param login the login of the user to retrieve
     * @return a ResponseEntity containing a message, a status code and the user
     */
    @GetMapping("/{login}")
    public HttpResponse getUser(@PathVariable String login) {
        User user = userDao.get(login).orElse(null);

        if (user == null) {
            return new HttpResponseBuilder()
                    .message(HttpMessage.USER_NOT_FOUND)
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return new HttpResponseBuilder()
                .message(HttpMessage.USER_FOUND)
                .data("user", user)
                .status(HttpStatus.OK)
                .build();
    }

    /**
     * Adds a new user to the system.
     *
     * @param user the CreateUserDto object containing the user information
     * @return a ResponseEntity containing a message, a status code and the user
     */
    @PostMapping()
    public HttpResponse addUser(@RequestBody CreateUserDto user) {
        if (userDao.get(user.getLogin()).isPresent()) {
            return new HttpResponseBuilder()
                    .message(HttpMessage.USER_ALREADY_EXISTS)
                    .status(HttpStatus.CONFLICT)
                    .build();
        }

        User newUser = new User(user.getLogin(), user.getSpecies(), user.getPassword(), user.getImage());
        userDao.save(newUser);

        return new HttpResponseBuilder()
                .message(HttpMessage.USER_CREATED_SUCCESSFULLY)
                .data("user", newUser)
                .status(HttpStatus.CREATED)
                .build();
    }

    /**
     * Updates the password of a user.
     *
     * @param login    the login of the user
     * @param password the new password
     * @return a ResponseEntity containing a message and a status code
     */
    @PutMapping("/{login}")
    public HttpResponse updatePassword(@PathVariable String login, @RequestBody String password) {
        Optional<User> optionalUser = userDao.get(login);

        if (optionalUser.isEmpty()) {
            return new HttpResponseBuilder()
                    .message(HttpMessage.USER_NOT_FOUND)
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        User user = optionalUser.get();
        user.setPassword(password);

        return new HttpResponseBuilder()
                .message(HttpMessage.PASSWORD_UPDATED_SUCCESSFULLY)
                .status(HttpStatus.OK)
                .build();
    }

    /**
     * Deletes a user by their login.
     *
     * @param login the login of the user to delete
     */
    @DeleteMapping("/{login}")
    public HttpResponse deleteUser(@PathVariable String login) {
        User user = userDao.get(login).orElse(null);

        if (user == null) {

            return new HttpResponseBuilder()
                    .message(HttpMessage.USER_NOT_FOUND)
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        userDao.delete(user);

        return new HttpResponseBuilder()
                .message(HttpMessage.USER_DELETED_SUCCESSFULLY)
                .status(HttpStatus.OK)
                .build();
    }
}
