package com.example.demo.repository;

import com.example.demo.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void testFindByEmail() {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("João");
        usuario.setEmail("joao@example.com");
        usuario.setSenha("123");

        usuarioRepository.save(usuario);

        Optional<Usuario> encontrado = usuarioRepository.findByEmail("joao@example.com");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("João");
    }
}
