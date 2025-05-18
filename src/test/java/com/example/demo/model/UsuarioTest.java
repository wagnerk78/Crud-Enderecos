package com.example.demo.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioTest {

    @Test
    void deveCriarUsuarioComConstrutorCompleto() {
        Usuario usuario = new Usuario("Wagner", "wagner@example.com", "123456");

        assertThat(usuario.getNome()).isEqualTo("Wagner");
        assertThat(usuario.getEmail()).isEqualTo("wagner@example.com");
        assertThat(usuario.getSenha()).isEqualTo("123456");
    }

    @Test
    void devePermitirSetarEObterAtributos() {
        Usuario usuario = new Usuario();
        UUID id = UUID.randomUUID();

        usuario.setId(id);
        usuario.setNome("Maria");
        usuario.setEmail("maria@example.com");
        usuario.setSenha("senha123");

        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getNome()).isEqualTo("Maria");
        assertThat(usuario.getEmail()).isEqualTo("maria@example.com");
        assertThat(usuario.getSenha()).isEqualTo("senha123");
    }

    @Test
    void deveCriarUsuarioComConstrutorInvalidoSemAtribuirValores() {
        Usuario usuario = new Usuario("mail", "senha123");

        assertThat(usuario.getNome()).isNull(); // o construtor não atribui nenhum valor
        assertThat(usuario.getEmail()).isNull();
        assertThat(usuario.getSenha()).isNull();
    }
}
