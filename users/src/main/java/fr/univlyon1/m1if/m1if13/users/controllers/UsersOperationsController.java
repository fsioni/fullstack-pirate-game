package fr.univlyon1.m1if.m1if13.users.controllers;

import fr.univlyon1.m1if.m1if13.users.dao.UserDao;
import fr.univlyon1.m1if.m1if13.users.models.User;
import fr.univlyon1.m1if.m1if13.users.utils.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
     * @param login    Le login de l'utilisateur. L'utilisateur doit avoir été créé préalablement et son login doit être présent dans le DAO.
     * @param password Le password à vérifier.
     * @return Une ResponseEntity avec le JWT dans le header "Authentication" si le login s'est bien passé, et le code de statut approprié (204, 401 ou 404).
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestParam("login") String login, @RequestParam("password") String password, @RequestHeader("Origin") String origin) {
        User user = userDao.get(login).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).build();
        }

        try {
            user.authenticate(password);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

        String jwt = JwtHelper.generateToken(login, password, origin);
        return ResponseEntity.status(204).header("Authentication", jwt).build();
    }

    /**
     * Réalise la déconnexion
     */
//    @PostMapping("/logout")
    // TODO

    /**
     * Méthode destinée au serveur Node pour valider l'authentification d'un utilisateur.
     *
     * @param token  Le token JWT qui se trouve dans le header "Authentication" de la requête
     * @param origin L'origine de la requête (pour la comparer avec celle du client, stockée dans le token JWT)
     * @return Une réponse vide avec un code de statut approprié (204, 400, 401).
     */
    @GetMapping("/authenticate")
    public ResponseEntity<Void> authenticate(@RequestParam("jwt") String jwt, @RequestParam("origin") String origin) {
        // TODO
        return null;
    }
}