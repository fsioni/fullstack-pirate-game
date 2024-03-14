package fr.univlyon1.m1if.m1if13.users.controllers;

import fr.univlyon1.m1if.m1if13.users.dao.UserDao;
import fr.univlyon1.m1if.m1if13.users.models.Species;
import fr.univlyon1.m1if.m1if13.users.models.User;
import fr.univlyon1.m1if.m1if13.users.services.http.HttpHeader;
import fr.univlyon1.m1if.m1if13.users.utils.JwtHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsersOperationsController.class)
class UsersOperationsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDao userDao;

    @Test
    void shouldAuthenticateUserAndReturnJwtOnSuccessfulLogin() throws Exception {
        String login = "userLogin";
        String password = "userPassword";
        String origin = "http://localhost";
        User user = new User(login, Species.VILLAGEOIS, password);

        when(userDao.get(login)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/user/login")
                        .param("login", login)
                        .param("password", password)
                        .header("Origin", origin))
                .andExpect(status().isOk())
                .andExpect(header().exists(String.valueOf(HttpHeader.AUTHENTICATION)));

        verify(userDao).get(login);
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExistOnLogin() throws Exception {
        when(userDao.get("nonExistentUser")).thenReturn(Optional.empty());

        mockMvc.perform(post("/user/login")
                        .param("login", "nonExistentUser")
                        .param("password", "anyPassword")
                        .header("Origin", "http://localhost"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnUnauthorizedWhenPasswordIsIncorrectOnLogin() throws Exception {
        String login = "userLogin";
        String wrongPassword = "wrongPassword";
        User user = new User(login, Species.VILLAGEOIS, "correctPassword");

        when(userDao.get(login)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/user/login")
                        .param("login", login)
                        .param("password", wrongPassword)
                        .header("Origin", "http://localhost"))
                .andExpect(status().isUnauthorized());
    }
}
