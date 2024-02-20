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

@RestController()
@RequestMapping("/user")
public class UsersOperationsController {

    UserDao userDao;


    @Autowired
    public UsersOperationsController(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Procédure de login utilisée par un utilisateur
     *
     * @param login    Le login de l'utilisateur. L'utilisateur doit avoir été créé
     *                 préalablement et son login doit être présent dans le DAO.
     * @param password Le password à vérifier.
     * @return Une ResponseEntity avec le JWT dans le header "Authentication" si le
     * login s'est bien passé, et le code de statut approprié (204, 401 ou
     * 404).
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
     * Logs out the user associated with the provided JWT token.
     *
     * @param jwt The JWT token used for authentication.
     * @return A ResponseEntity with a status code indicating the result of the logout operation.
     * Returns 204 (No Content) if the logout is successful.
     * Returns 404 (Not Found) if the user associated with the provided JWT token is not found.
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
     * Méthode destinée au serveur Node pour valider l'authentification d'un
     * utilisateur.
     *
     * @param token  Le token JWT qui se trouve dans le header "Authentication" de
     *               la requête
     * @param origin L'origine de la requête (pour la comparer avec celle du client,
     *               stockée dans le token JWT)
     * @return Une réponse vide avec un code de statut approprié (204, 400, 401).
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