package fr.univlyon1.m1if.m1if13.users.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.fail;

class UserTest {
    User anny, francois;

    @BeforeEach
    void setUp() {
        anny = new User("Anny Bonney", Species.PIRATE, "milsabor");
        francois = new User("François Perrin", Species.VILLAGEOIS, "ChaussureNoire");
    }

    @Test
    void getLogin() {
        assert (anny.getLogin().equals("Anny Bonney"));
        assert (francois.getLogin().equals("François Perrin"));
    }

    @Test
    void getSpecies() {
        assert (anny.getSpecies().equals(Species.PIRATE));
        assert (francois.getSpecies().equals(Species.VILLAGEOIS));
    }

    @Test
    void setPassword() {
        anny.setPassword("ectoplasme");
        anny.authenticate("ectoplasme");
        assert (true);
    }

    @Test
    void isConnected() {
        anny.authenticate("milsabor");
        assert (anny.isConnected());
        anny.disconnect();
        assert (!anny.isConnected());
    }

    @Test
    void authenticate() {
        anny.authenticate("milsabor");
        assert (true);

        francois.authenticate("milsabor");
        fail("Mot de passe incorrect");
    }

    @Test
    void disconnect() {
        anny.authenticate("milsabor");
        anny.disconnect();
        assert (!anny.isConnected());
    }
}