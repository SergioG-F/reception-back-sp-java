package com.pe.recepcion.jwt;

import com.pe.recepcion.model.UsuarioEntity;
import com.pe.recepcion.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        UsuarioEntity user = repo.findByCorreo(input).or(() -> repo.findByUserName(input)).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new User(
                user.getUserName(), // puedes usar user.getUserName() si prefieres
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRol().name()))
        );

    }
}
