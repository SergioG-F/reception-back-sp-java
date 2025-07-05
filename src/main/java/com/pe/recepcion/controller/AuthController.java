package com.pe.recepcion.controller;

import com.pe.recepcion.dto.request.LoginRequest;
import com.pe.recepcion.dto.response.AuthResponse;
import com.pe.recepcion.jwt.JwtUtil;
import com.pe.recepcion.model.UsuarioEntity;
import com.pe.recepcion.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepo;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Autenticación con correo o username
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
            );

            UsuarioEntity usuario = userRepo.findByCorreo(request.getLogin())
                    .or(() -> userRepo.findByUserName(request.getLogin())) // si no es correo, busca por username
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            // usa username como identificador principal
            UserDetails userDetails = new User(
                    usuario.getUserName(),
                    usuario.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()))
            );

            String token = jwtUtil.generateToken(userDetails);
            //aqui lo devolvemos en el postman el response del token todos sus atribtuso
            return ResponseEntity.ok(new AuthResponse(
                    token,
                    usuario.getCorreo(),
                    usuario.getUserName(),
                    usuario.getNombreCompleto(),
                    usuario.getRol().name()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("❌ Login inválido: " + e.getMessage());
        }
    }

}
