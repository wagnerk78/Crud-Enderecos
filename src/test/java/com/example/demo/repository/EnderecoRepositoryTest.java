package com.example.demo.repository;

import com.example.demo.model.Endereco;
import com.example.demo.model.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
class EnderecoRepositoryTest {

    @MockBean
    private EnderecoRepository enderecoRepository;

    @Test
    void findByUsuario_ShouldReturnListOfEnderecos() {

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());

        Endereco endereco1 = new Endereco();
        endereco1.setUsuario(usuario);
        Endereco endereco2 = new Endereco();
        endereco2.setUsuario(usuario);

        List<Endereco> expectedEnderecos = Arrays.asList(endereco1, endereco2);

        when(enderecoRepository.findByUsuario(usuario)).thenReturn(expectedEnderecos);


        List<Endereco> result = enderecoRepository.findByUsuario(usuario);


        assertEquals(2, result.size());
        assertTrue(result.containsAll(expectedEnderecos));
        verify(enderecoRepository, times(1)).findByUsuario(usuario);
    }

    @Test
    @Transactional
    void deleteByUsuarioId_ShouldDeleteEnderecos() {

        UUID usuarioId = UUID.randomUUID();

        doNothing().when(enderecoRepository).deleteByUsuarioId(usuarioId);


        enderecoRepository.deleteByUsuarioId(usuarioId);


        verify(enderecoRepository, times(1)).deleteByUsuarioId(usuarioId);
    }

    @Test
    void findByUsuario_WhenNoEnderecos_ShouldReturnEmptyList() {

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());

        when(enderecoRepository.findByUsuario(usuario)).thenReturn(List.of());


        List<Endereco> result = enderecoRepository.findByUsuario(usuario);


        assertTrue(result.isEmpty());
        verify(enderecoRepository, times(1)).findByUsuario(usuario);
    }
}