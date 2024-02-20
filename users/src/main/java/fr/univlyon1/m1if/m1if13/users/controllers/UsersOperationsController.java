package fr.univlyon1.m1if.m1if13.users.controllers;

import fr.univlyon1.m1if.m1if13.users.dao.UserDao;
import fr.univlyon1.m1if.m1if13.users.models.User;
import fr.univlyon1.m1if.m1if13.users.services.http.HttpHeader;
import fr.univlyon1.m1if.m1if13.users.services.http.HttpMessage;
import fr.univlyon1.m1if.m1if13.users.services.http.HttpResponse;
import fr.univlyon1.m1if.m1if13.users.services.http.HttpResponseBuilder;
import fr.univlyon1.m1if.m1if13.users.utils.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

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
    @PostMapping("/login")
    public HttpResponse login(@RequestParam("login") String login, @RequestParam("password") String password,
                              @RequestHeader("Origin") String origin) {
        User user = userDao.get(login).orElse(null);
        if (user == null) {
            return new HttpResponseBuilder()
                    .message(HttpMessage.USER_NOT_FOUND)
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        try {
            user.authenticate(password);
        } catch (AuthenticationException e) {
            return new HttpResponseBuilder()
                    .message(HttpMessage.WRONG_PASSWORD)
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

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
    @PostMapping("/logout")
    public HttpResponse logout(@RequestHeader("Authentication") String jwt) {
        String login = JwtHelper.getLogin(jwt);
        User user = userDao.get(login).orElse(null);
        if (user == null) {
            return new HttpResponseBuilder()
                    .message(HttpMessage.USER_NOT_FOUND)
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        user.disconnect();

        return new HttpResponseBuilder()
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    /**
     * Authenticates a user by verifying the provided JWT token and origin.
     *
     * @param jwt    the JWT token used for authentication
     * @param origin the origin of the request
     * @return an HttpResponse with an appropriate status code and message
     */
    @GetMapping("/authenticate")
    public HttpResponse authenticate(@RequestParam("jwt") String jwt, @RequestParam("origin") String origin) {
        if (jwt == null || jwt.isEmpty()) {
            return new HttpResponseBuilder()
                    .message(HttpMessage.JWT_MISSING)
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        if (origin == null || origin.isEmpty()) {
            return new HttpResponseBuilder()
                    .message(HttpMessage.ORIGIN_MISSING)
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        if (JwtHelper.verifyToken(jwt, origin)) {
            return new HttpResponseBuilder()
                    .status(HttpStatus.NO_CONTENT)
                    .build();
        }

        return new HttpResponseBuilder()
                .message(HttpMessage.WRONG_PASSWORD)
                .status(HttpStatus.UNAUTHORIZED)
                .build();
    }
}