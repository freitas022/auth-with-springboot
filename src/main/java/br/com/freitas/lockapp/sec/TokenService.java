package br.com.freitas.lockapp.sec;

import br.com.freitas.lockapp.dto.TokenDto;
import br.com.freitas.lockapp.exceptions.TokenGenerationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
    private static final String ISSUER = "lockapp-api";

    @Value("${api.secret.key}")
    private String secretKey;

    @Value("${api.token.expiration.hours}")
    private long tokenExpirationHours;

    public TokenDto generateToken(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("Authentication or username is null");
        }

        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            String accessToken = JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(authentication.getName())
                    .withExpiresAt(Instant.ofEpochSecond(tokenExpirationHours * 3600).atZone(ZoneOffset.UTC).toInstant())
                    .sign(algorithm);
            logger.info("Token generated for user: {}", authentication.getName());
            return new TokenDto(accessToken);
        } catch (JWTCreationException exception) {
            logger.error("Error while generating token", exception);
            throw new TokenGenerationException("Error while generating token", exception);
        }
    }

    public Optional<String> validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return Optional.empty();
        }

        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            String subject = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
            logger.info("Token validated successfully");
            return Optional.ofNullable(subject);
        } catch (JWTVerificationException exception) {
            logger.warn("Invalid token", exception);
            return Optional.empty();
        }
    }
}
