package fr.univlyon1.m1if.m1if13.users.models;

import javax.naming.AuthenticationException;

import fr.univlyon1.m1if.m1if13.users.exceptions.MismatchPasswordException;
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
        assert(anny.getLogin().equals("Anny Bonney"));
        assert(francois.getLogin().equals("François Perrin"));
    }

    @Test
    void getSpecies() {
        assert(anny.getSpecies().equals(Species.PIRATE));
        assert(francois.getSpecies().equals(Species.VILLAGEOIS));
    }

    @Test
    void setPassword() {
        anny.setPassword("ectoplasme");
        try {
            anny.authenticate("ectoplasme");
            assert(true);
        } catch (MismatchPasswordException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void isConnected() {
        try {
            anny.authenticate("milsabor");
            assert(anny.isConnected());
            anny.disconnect();
            assert(!anny.isConnected());
        } catch (MismatchPasswordException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void authenticate() {
        try {
            anny.authenticate("milsabor");
            assert(true);
        } catch (MismatchPasswordException e) {
            fail(e.getMessage());
        }

        try {
            francois.authenticate("milsabor");
            fail("Mot de passe incorrect");
        } catch (MismatchPasswordException e) {
            assert(true);
        }
    }

    @Test
    void disconnect() {
        try {
            anny.authenticate("milsabor");
            anny.disconnect();
            assert(!anny.isConnected());
        } catch (MismatchPasswordException e) {
            fail(e.getMessage());
        }
    }
}