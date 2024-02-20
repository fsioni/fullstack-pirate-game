package fr.univlyon1.m1if.m1if13.users.controllers;

import fr.univlyon1.m1if.m1if13.users.dao.UserDao;
import fr.univlyon1.m1if.m1if13.users.dto.CreateUserDto;
import fr.univlyon1.m1if.m1if13.users.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
     * @return a set of strings representing the usernames of all users
     */
    @GetMapping()
    public Set<String> getUsers() {
        return userDao.getAll();
    }

    /**
     * Retrieves a user by their login.
     *
     * @param login the login of the user to retrieve
     * @return an Optional containing the user if found, or an empty Optional if not found
     */
    @GetMapping("/{login}")
    public Optional<User> getUser(@PathVariable String login) {
        User user = userDao.get(login).orElse(null);
        if (user == null) {
            return Optional.empty();
        } else {
            return Optional.of(user);
        }
    }

    /**
     * Adds a new user to the system.
     *
     * @param user the CreateUserDto object containing the user information
     * @return a ResponseEntity with the appropriate HTTP status code
     */
    @PostMapping()
    public ResponseEntity<Void> addUser(@RequestBody CreateUserDto user) {
        if (userDao.get(user.getLogin()).isPresent()) {
            return ResponseEntity.status(409).build();
        }
        userDao.save(new User(user.getLogin(), user.getSpecies(), user.getPassword(), user.getImage()));
        return ResponseEntity.status(201).build();
    }

    /**
     * Updates the password of a user.
     *
     * @param login    the login of the user
     * @param password the new password
     */
    @PutMapping("/{login}")
    public void updatePassword(@PathVariable String login, @RequestBody String password) {
        userDao.get(login).ifPresent(user -> user.setPassword(password));
    }

    /**
     * Deletes a user by their login.
     *
     * @param login the login of the user to delete
     */
    @DeleteMapping("/{login}")
    public void deleteUser(@PathVariable String login) {
        User user = userDao.get(login).orElse(null);
        if (user != null) {
            userDao.delete(user);
        }
    }
}
