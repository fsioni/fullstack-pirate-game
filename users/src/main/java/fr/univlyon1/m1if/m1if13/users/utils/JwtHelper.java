package fr.univlyon1.m1if.m1if13.users.utils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;

public class JwtHelper {
    private static final String SECRET = "monsecret2024";
    private static final String ISSUER = "mif13";
    private static final long LIFE_TIME = 1800000;


    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);


    public static String generateToken(String login, String password, String origin) {
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(login)
                .withAudience(origin)
                .withExpiresAt(getExpirationDate())
                .sign(ALGORITHM);
    }

    private static Date getExpirationDate() {
        return new Date(System.currentTimeMillis() + LIFE_TIME);
    }
}
