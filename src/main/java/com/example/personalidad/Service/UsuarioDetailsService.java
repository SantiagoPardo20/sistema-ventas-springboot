package com.example.personalidad.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.example.personalidad.Repository.UsuarioRepository;
import com.example.personalidad.model.Usuario;
@Service
public class UsuarioDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repo;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Usuario u = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.builder()
                .username(u.getEmail())
                .password(u.getPassword())   // contraseña ENCRIPTADA (BCrypt)
                .roles(u.getRol())           // ADMIN o EMPLEADO
                .disabled(!u.isActivo())
                .build();
    }
}
