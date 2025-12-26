package com.web.weblab4.service;

import com.web.weblab4.model.User;
import com.web.weblab4.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Stateless
public class AuthService {
    private final String secret = "pozhaluista_daite_zachet_po_webu";
    private final Key key = Keys.hmacShaKeyFor(secret.getBytes());

    @Inject
    private UserRepository userRepository;

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String password, String hashed) {
        return BCrypt.checkpw(password, hashed);
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }

    public void register(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashPassword(password));

        userRepository.save(user);
    }

    public String validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();

        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired JWT token");
        }
    }
}
