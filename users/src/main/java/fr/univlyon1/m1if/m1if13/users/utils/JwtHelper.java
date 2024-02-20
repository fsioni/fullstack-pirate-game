package fr.univlyon1.m1if.m1if13.users.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;

public class JwtHelper {
    private static final String SECRET = "monsecret2024";
    private static final String ISSUER = "mif13";
    private static final long LIFE_TIME = 1800000;

    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);

    /**
     * Generates a JWT token for the given login, password, and origin.
     *
     * @param login    the login of the user
     * @param password the password of the user
     * @param origin   the origin of the request
     * @return the generated JWT token
     */
    public static String generateToken(String login, String password, String origin) {
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(login)
                .withAudience(origin)
                .withExpiresAt(getExpirationDate())
                .sign(ALGORITHM);
    }

    /**
     * Returns the expiration date for the JWT token.
     *
     * @return the expiration date as a Date object
     */
    private static Date getExpirationDate() {
        return new Date(System.currentTimeMillis() + LIFE_TIME);
    }

    /**
     * Verifies the validity of a JWT token.
     *
     * @param token  the JWT token to verify
     * @param origin the expected origin of the token
     * @return true if the token is valid, false otherwise
     */
    public static boolean verifyToken(String token, String origin) {
        try {
            JWT.require(ALGORITHM)
                    .withIssuer(ISSUER)
                    .withAudience(origin)
                    .build()
                    .verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Retrieves the login from a JWT token.
     *
     * @param token the JWT token
     * @return the login extracted from the token
     */
    public static String getLogin(String token) {
        return JWT.decode(token).getSubject();
    }
}
