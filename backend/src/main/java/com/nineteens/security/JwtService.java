package com.nineteens.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nineteens.config.AppProperties;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final AppProperties properties;
    private final byte[] secret;

    public JwtService(AppProperties properties) {
        this.properties = properties;
        this.secret = padSecret(properties.getJwt().getSecret());
    }

    public String generateAccessToken(UserPrincipal principal) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(properties.getJwt().getExpirationMs());
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(String.valueOf(principal.getId()))
                .claim("email", principal.getUsername())
                .claim("role", principal.getRole().name())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(exp))
                .build();
        try {
            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            jwt.sign(new MACSigner(secret));
            return jwt.serialize();
        } catch (JOSEException ex) {
            throw new IllegalStateException("Unable to create access token", ex);
        }
    }

    public JWTClaimsSet parse(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            if (!jwt.verify(new MACVerifier(secret))) {
                throw new JOSEException("Invalid signature");
            }
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            Date exp = claims.getExpirationTime();
            if (exp == null || exp.toInstant().isBefore(Instant.now())) {
                throw new JOSEException("Token expired");
            }
            return claims;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid access token", ex);
        }
    }

    public long expirationMs() {
        return properties.getJwt().getExpirationMs();
    }

    private static byte[] padSecret(String secret) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length >= 32) {
            return bytes;
        }
        byte[] padded = new byte[32];
        System.arraycopy(bytes, 0, padded, 0, bytes.length);
        return padded;
    }
}
