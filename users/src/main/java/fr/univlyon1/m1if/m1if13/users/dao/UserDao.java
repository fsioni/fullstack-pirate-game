package fr.univlyon1.m1if.m1if13.users.dao;

import fr.univlyon1.m1if.m1if13.users.models.Species;
import fr.univlyon1.m1if.m1if13.users.models.User;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserDao implements Dao<User> {
    private final List<User> users = new ArrayList<>();

    public UserDao() {
        users.add(new User("adm", Species.ADMIN, "adm"));
        users.add(new User("player1", Species.PIRATE, "password1", "pirate-1.png"));
        users.add(new User("player2", Species.PIRATE, "password2"));
        users.add(new User("player3", Species.VILLAGEOIS, "password3", "villageois-1.png"));
        users.add(new User("player4", Species.VILLAGEOIS, "password4"));
    }

    @Override
    public Optional<User> get(String login) {
        return users.stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst();
    }

    @Override
    public Set<String> getAll() {
        return users.stream().map(User::getLogin).collect(Collectors.toSet());
    }

    @Override
    public void save(User user) {
        users.add(user);
    }

    @Override
    public void update(User user, String[] params) {
        user.setSpecies(Objects.requireNonNull(Species.valueOf(params[0])));
        user.setPassword(Objects.requireNonNull(params[1]));
    }

    @Override
    public void delete(User user) {
        users.remove(user);
    }
}
