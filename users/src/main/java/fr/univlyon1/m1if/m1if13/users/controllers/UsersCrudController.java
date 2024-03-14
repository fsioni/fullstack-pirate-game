package fr.univlyon1.m1if.m1if13.users.controllers;

import fr.univlyon1.m1if.m1if13.users.dao.UserDao;
import fr.univlyon1.m1if.m1if13.users.dto.CreateUserDto;
import fr.univlyon1.m1if.m1if13.users.exceptions.LoginAlreadyExistsException;
import fr.univlyon1.m1if.m1if13.users.exceptions.UserNotFoundException;
import fr.univlyon1.m1if.m1if13.users.exceptions.UsersNotFoundException;
import fr.univlyon1.m1if.m1if13.users.models.User;
import fr.univlyon1.m1if.m1if13.users.services.http.HttpMessage;
import fr.univlyon1.m1if.m1if13.users.services.http.HttpResponse;
import fr.univlyon1.m1if.m1if13.users.services.http.HttpResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * This class represents a controller for CRUD operations on users.
 */
@Controller()
@RestControllerAdvice
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
    @GetMapping(produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
    })
    public HttpResponse getUsers() {
        Set<String> users = userDao.getAll();
        if (users.isEmpty()) {
            throw new UsersNotFoundException();
        }

        return new HttpResponseBuilder()
                .message(HttpMessage.USERS_FOUND)
                .data("users", users)
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String getUsersHtml(Model model) {
        Set<String> users = userDao.getAll();

        if (users.isEmpty()) {
            model.addAttribute("message", HttpMessage.NO_USERS_FOUND);
            model.addAttribute("status", HttpStatus.NOT_FOUND);
            return "error";
        }

        model.addAttribute("users", users);
        return "users";
    }

    /**
     * Retrieves a user by their login.
     *
     * @param login the login of the user to retrieve
     * @return a ResponseEntity containing a message, a status code and the user
     */
    @GetMapping(value = "/{login}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
            })
    public HttpResponse getUser(@PathVariable String login) {
        User user = userDao.get(login).orElseThrow(UserNotFoundException::new);

        return new HttpResponseBuilder()
                .message(HttpMessage.USER_FOUND)
                .data("user", user)
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping(value = "/{login}",
            produces = MediaType.TEXT_HTML_VALUE)
    public String getUserHtml(@PathVariable String login, Model model) {
        User user = userDao.get(login).orElseThrow(UserNotFoundException::new);

        model.addAttribute("user", user);
        return "user";
    }

    /**
     * Adds a new user to the system.
     *
     * @param user the CreateUserDto object containing the user information
     * @return an HttpResponse object
     */
    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public HttpResponse addUserUrlEncoded(CreateUserDto user) {
        return addUser(user);
    }

    /**
     * Adds a new user to the system.
     *
     * @param user the CreateUserDto object containing the user information
     * @return a ResponseEntity containing a message, a status code and the user
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public HttpResponse addUserJson(@RequestBody CreateUserDto user) {
        return addUser(user);
    }

    /**
     * Adds a new user to the system.
     *
     * @param user the CreateUserDto object containing the user information
     * @return an HttpResponse object containing a message, a status code, and the user
     */
    private HttpResponse addUser(CreateUserDto user) {
        if (userDao.get(user.getLogin()).isPresent()) {
            throw new LoginAlreadyExistsException();
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
     * @return an instance of HttpResponse containing a message and a status code
     */
    @PutMapping(path = "/{login}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public HttpResponse updatePasswordUrlEncoded(@PathVariable String login, String password) {
        return updatePassword(login, password);
    }

    /**
     * Updates the password of a user.
     *
     * @param login    the login of the user
     * @param password the new password
     * @return a ResponseEntity containing a message and a status code
     */
    @PutMapping(path = "/{login}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public HttpResponse updatePasswordJson(@PathVariable String login, @RequestBody String password) {
        return updatePassword(login, password);
    }

    private HttpResponse updatePassword(String login, String password) {
        User user = userDao.get(login).orElseThrow(UserNotFoundException::new);
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
        User user = userDao.get(login).orElseThrow(UserNotFoundException::new);

        userDao.delete(user);

        return new HttpResponseBuilder()
                .message(HttpMessage.USER_DELETED_SUCCESSFULLY)
                .status(HttpStatus.OK)
                .build();
    }
}
