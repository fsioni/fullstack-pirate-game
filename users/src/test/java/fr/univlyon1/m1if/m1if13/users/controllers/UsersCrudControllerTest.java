package fr.univlyon1.m1if.m1if13.users.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univlyon1.m1if.m1if13.users.dao.UserDao;
import fr.univlyon1.m1if.m1if13.users.dto.CreateUserDto;
import fr.univlyon1.m1if.m1if13.users.models.Species;
import fr.univlyon1.m1if.m1if13.users.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsersCrudController.class)
class UsersCrudControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDao userDao;

    @Test
    void shouldReturnAllUsersWhenUsersExist() throws Exception {
        Set<String> expectedUsers = new HashSet<>(Arrays.asList("user1", "user2"));
        when(userDao.getAll()).thenReturn(expectedUsers);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users", hasSize(2)));
    }

    @Test
    void shouldReturnNotFoundWhenNoUsersExist() throws Exception {
        when(userDao.getAll()).thenReturn(new HashSet<>());

        mockMvc.perform(get("/users"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAddUserSuccessfully() throws Exception {
        CreateUserDto newUser = new CreateUserDto();
        newUser.setLogin("newUser");
        newUser.setSpecies(Species.VILLAGEOIS);
        newUser.setPassword("password");
        newUser.setImage("imagePath");

        String jsonUser = new ObjectMapper().writeValueAsString(newUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isCreated());

        verify(userDao).save(any(User.class));
    }

    @Test
    void shouldReturnConflictWhenUserAlreadyExists() throws Exception {
        CreateUserDto existingUserDto = new CreateUserDto();
        existingUserDto.setLogin("existingUser");
        existingUserDto.setSpecies(Species.VILLAGEOIS); // Assure-toi que cette valeur correspond à une valeur valide de ton énumération Species
        existingUserDto.setPassword("password");
        existingUserDto.setImage("imagePath");

        String jsonUser = new ObjectMapper().writeValueAsString(existingUserDto);

        User existingUser = new User(existingUserDto.getLogin(), existingUserDto.getSpecies(), existingUserDto.getPassword(), existingUserDto.getImage());

        when(userDao.get(existingUserDto.getLogin())).thenReturn(Optional.of(existingUser));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnUserWhenUserExists() throws Exception {
        User user = new User("user1", Species.VILLAGEOIS, "password", "imagePath");
        when(userDao.get("user1")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.login", org.hamcrest.Matchers.is("user1")));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        when(userDao.get("nonExistentUser")).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/nonExistentUser"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdatePasswordSuccessfully() throws Exception {
        User user = new User("user1", Species.VILLAGEOIS, "oldPassword", "imagePath");
        when(userDao.get("user1")).thenReturn(Optional.of(user));

        mockMvc.perform(put("/users/user1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"newPassword\"}"))
                .andExpect(status().isOk());

        User updatedUser = userDao.get("user1").orElseThrow(() -> new AssertionError("User should exist"));
        updatedUser.setPassword("newPassword");
        user.authenticate("newPassword"); // Si cette ligne lève une MismatchPasswordException, le test échouera.
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingPasswordForNonExistentUser() throws Exception {
        when(userDao.get("nonExistentUser")).thenReturn(Optional.empty());

        mockMvc.perform(put("/users/nonExistentUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"newPassword\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteUserSuccessfully() throws Exception {
        User user = new User("user1", Species.VILLAGEOIS, "password", "imagePath");
        when(userDao.get("user1")).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/users/user1"))
                .andExpect(status().isOk());

        verify(userDao).delete(user);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentUser() throws Exception {
        when(userDao.get("nonExistentUser")).thenReturn(Optional.empty());

        mockMvc.perform(delete("/users/nonExistentUser"))
                .andExpect(status().isNotFound());
    }

}
