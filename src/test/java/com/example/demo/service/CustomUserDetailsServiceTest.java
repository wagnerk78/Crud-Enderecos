package com.example.demo.service;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private UsuarioRepository usuarioRepository;
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        userDetailsService = new CustomUserDetailsService(usuarioRepository);
    }

    @Test
    void loadUserByUsername_AdminUser_ReturnsAdminDetails() {

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin@admin.com");

        assertNotNull(userDetails);
        assertEquals("admin@admin.com", userDetails.getUsername());
        assertTrue(new BCryptPasswordEncoder().matches("admin", userDetails.getPassword()));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_ExistingUser_ReturnsUserDetails() {

        Usuario usuario = new Usuario();
        usuario.setEmail("user@test.com");
        usuario.setSenha(new BCryptPasswordEncoder().encode("senha123"));

        when(usuarioRepository.findByEmail("user@test.com")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = userDetailsService.loadUserByUsername("user@test.com");

        assertNotNull(userDetails);
        assertEquals("user@test.com", userDetails.getUsername());
        assertEquals(usuario.getSenha(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }


    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {

        when(usuarioRepository.findByEmail("naoexiste@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("naoexiste@test.com"));
    }
}
