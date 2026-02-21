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
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    public String generateToken(String id) {
        return Jwts.builder()
                .setSubject(id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }

    public Map<String, String> register(User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty() || user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("Поля username и пароль не должны быть пустыми")
                            .type(MediaType.TEXT_PLAIN)
                            .build()
            );
        }

        User existUser = userRepository.findByUsername(user.getUsername());
        if (existUser != null) {
            throw new WebApplicationException(
                    Response.status(Response.Status.CONFLICT)
                            .entity("Пользователь с таким username уже существует!")
                            .type(MediaType.TEXT_PLAIN)
                            .build()
            );
        }

        if (user.getPassword().length() < 8) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("Пароль должен состоять как минимум из 8 символов")
                            .type(MediaType.TEXT_PLAIN)
                            .build()
            );
        }


        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(hashPassword(user.getPassword()));

        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new WebApplicationException(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Проблема с базой данных, юзер не засейвился")
                            .type(MediaType.TEXT_PLAIN)
                            .build()
            );
        }

        Map<String, String> response = new HashMap<>();
        response.put("token", generateToken(user.getId().toString()));
        return response;
    }

    public Map<String, String> login(User user) {
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser == null) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("Пользователя с таким username не существует")
                            .type(MediaType.TEXT_PLAIN)
                            .build()
            );
        }

        if (checkPassword(user.getPassword(), existingUser.getPassword())) {
            String token = generateToken(user.getId().toString());
            return Map.of("token", token);
        } else {
            throw new WebApplicationException(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("Пароль неверный!")
                            .type(MediaType.TEXT_PLAIN)
                            .build()
            );

        }
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
            throw new WebApplicationException(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Invalid or expired JWT token")
                            .type(MediaType.TEXT_PLAIN)
                            .build()
            );
        }
    }
}
