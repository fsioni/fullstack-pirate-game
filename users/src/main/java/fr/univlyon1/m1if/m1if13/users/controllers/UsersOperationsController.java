package fr.univlyon1.m1if.m1if13.users.controllers;

import fr.univlyon1.m1if.m1if13.users.dao.UserDao;
import fr.univlyon1.m1if.m1if13.users.exceptions.InvalidJwtException;
import fr.univlyon1.m1if.m1if13.users.exceptions.NoJwtProvidedException;
import fr.univlyon1.m1if.m1if13.users.exceptions.NoOriginProvidedException;
import fr.univlyon1.m1if.m1if13.users.exceptions.UserNotFoundException;
import fr.univlyon1.m1if.m1if13.users.models.User;
import fr.univlyon1.m1if.m1if13.users.services.http.HttpHeader;
import fr.univlyon1.m1if.m1if13.users.services.http.HttpMessage;
import fr.univlyon1.m1if.m1if13.users.services.http.HttpResponse;
import fr.univlyon1.m1if.m1if13.users.services.http.HttpResponseBuilder;
import fr.univlyon1.m1if.m1if13.users.utils.JwtHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller class for user operations.
 */
@RestController()
@RequestMapping("/user")
public class UsersOperationsController {

    UserDao userDao;


    @Autowired
    public UsersOperationsController(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Authenticates a user by verifying the provided login and password,
     * and generates a JWT token for authentication.
     *
     * @param login    the login of the user
     * @param password the password of the user
     * @param origin   the origin of the request
     * @return an HttpResponse with the appropriate status code, message, and JWT token as the authentication header
     */
    @Operation(summary = "Authentifie un utilisateur et génère un JWT",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Connexion réussie",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé",
                            content = @Content()),
                    @ApiResponse(responseCode = "401", description = "Authentification échouée",
                            content = @Content())
            }
    )
    @PostMapping("/login")
    @CrossOrigin(origins = {"http://localhost", "http://192.168.75.23", "https://192.168.75.23", "http://localhost:3376"})
    public HttpResponse login(@RequestParam("login") String login, @RequestParam("password") String password,
                              @RequestHeader("Origin") String origin) {
        User user = userDao.get(login).orElseThrow(UserNotFoundException::new);

        user.authenticate(password);

        String jwt = JwtHelper.generateToken(login, password, origin);

        return new HttpResponseBuilder()
                .status(HttpStatus.OK)
                .message(HttpMessage.LOGIN_SUCCESS)
                .header(HttpHeader.AUTHENTICATION, jwt)
                .build();
    }

    /**
     * Logs out a user by disconnecting them.
     *
     * @param jwt the JWT token used for authentication
     * @return an HttpResponse with the appropriate status code
     */
    @Operation(summary = "Déconnecte un utilisateur",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Déconnexion réussie"),
                    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé",
                            content = @Content())
            }
    )
    @PostMapping("/logout")
    @CrossOrigin(origins = {"http://localhost", "http://192.168.75.23", "https://192.168.75.23", "http://localhost:3376"})
    public HttpResponse logout(@RequestHeader("Authentication") String jwt) {
        String login = JwtHelper.getLogin(jwt);
        User user = userDao.get(login).orElseThrow(UserNotFoundException::new);

        user.disconnect();

        throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }

    /**
     * Authenticates a user by verifying the provided JWT token and origin.
     *
     * @param jwt    the JWT token used for authentication
     * @param origin the origin of the request
     * @return an HttpResponse with an appropriate status code and message
     */
    @Operation(summary = "Authentifie un utilisateur via JWT",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authentification réussie",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "400", description = "JWT ou origine non fourni",
                            content = @Content()),
                    @ApiResponse(responseCode = "401", description = "JWT invalide",
                            content = @Content()),
                    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé",
                            content = @Content())
            }
    )
    @GetMapping("/authenticate")
    public HttpResponse authenticate(@RequestParam("jwt") String jwt, @RequestParam("origin") String origin) {
        if (jwt == null || jwt.isEmpty()) {
            throw new NoJwtProvidedException();
        }

        if (origin == null || origin.isEmpty()) {
            throw new NoOriginProvidedException();
        }

        if (JwtHelper.verifyToken(jwt, origin)) {
            User user = userDao.get(JwtHelper.getLogin(jwt)).orElseThrow(UserNotFoundException::new);

            return new HttpResponseBuilder()
                    .status(HttpStatus.OK)
                    .data("user", user)
                    .build();
        }

        throw new InvalidJwtException();
    }
}