package com.pe.recepcion.jwt;

import com.pe.recepcion.model.UsuarioEntity;
import com.pe.recepcion.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final UserRepository repo;

    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("jwt-secreto-clave-jwt-secreto-clave".getBytes(StandardCharsets.UTF_8));

//    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);


    public String generateToken(UserDetails userDetails) {
        UsuarioEntity usuario = repo.findByCorreo(userDetails.getUsername())
                .or(() -> repo.findByUserName(userDetails.getUsername()))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        //Mostrar el token
        return Jwts.builder()
                .setSubject(usuario.getUserName())
                .claim("id", usuario.getId())
                .claim("correo", usuario.getCorreo())
                .claim("nombreCompleto", usuario.getNombreCompleto())
                .claim("username", usuario.getUserName())
                .claim("roles", userDetails.getAuthorities())
                .setIssuedAt(new Date())
                 //1000 ms → 1 segundo,60 → 1 ,minuto,60 → 1 hora, 24 horas
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractCorreo(String token) {
        return extractAllClaims(token).get("correo", String.class);
    }

    public String extractUsernameFromClaim(String token) {
        return extractAllClaims(token).get("username", String.class);
    }
}
