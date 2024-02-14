package fr.univlyon1.m1if.m1if13.users.controllers;

import fr.univlyon1.m1if.m1if13.users.dao.UserDao;
import fr.univlyon1.m1if.m1if13.users.dto.CreateUserDto;
import fr.univlyon1.m1if.m1if13.users.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController()
@RequestMapping("/users")
public class UsersCrudController {
    UserDao userDao;

    @Autowired
    public UsersCrudController(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping()
    public Set<String> getUsers() {
        return userDao.getAll();
    }

    @GetMapping("/{login}")
    public Optional<User> getUser(@PathVariable String login) {
        User user = userDao.get(login).orElse(null);
        if (user == null) {
            return Optional.empty();
        } else {
            return Optional.of(user);
        }
    }

    @PostMapping()
    public void addUser(@RequestBody CreateUserDto user) {
        userDao.save(new User(user.getLogin(), user.getSpecies(), user.getPassword(), user.getImage()));
    }

    @PutMapping("/{login}")
    public void updatePassword(@PathVariable String login, @RequestBody String password) {
        userDao.get(login).ifPresent(user -> user.setPassword(password));
    }

    @DeleteMapping("/{login}")
    public void deleteUser(@PathVariable String login) {
        User user = userDao.get(login).orElse(null);
        if (user != null) {
            userDao.delete(user);
        }
    }
}
