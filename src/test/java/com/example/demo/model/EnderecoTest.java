package com.example.demo.model;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class EnderecoTest {

    @Test
    void testConstrutorPadrao() {

        Endereco endereco = new Endereco();


        assertNotNull(endereco);
        assertNull(endereco.getId());
        assertNull(endereco.getLogradouro());
        assertNull(endereco.getUsuario());
    }

    @Test
    void testConstrutorComParametros() {

        String logradouro = "Rua Teste";
        String numero = "123";
        String complemento = "Apto 101";
        String bairro = "Centro";
        String cidade = "São Paulo";
        String estado = "SP";
        String cep = "01001000";
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());


        Endereco endereco = new Endereco(logradouro, numero, complemento,
                bairro, cidade, estado, cep, usuario);


        assertNotNull(endereco);
        assertEquals(logradouro, endereco.getLogradouro());
        assertEquals(numero, endereco.getNumero());
        assertEquals(complemento, endereco.getComplemento());
        assertEquals(bairro, endereco.getBairro());
        assertEquals(cidade, endereco.getCidade());
        assertEquals(estado, endereco.getEstado());
        assertEquals(cep, endereco.getCep());
        assertEquals(usuario, endereco.getUsuario());
    }

    @Test
    void testGettersAndSetters() {

        Endereco endereco = new Endereco();
        UUID id = UUID.randomUUID();
        String logradouro = "Av. Paulista";
        String numero = "1000";
        String complemento = "Conjunto 12";
        String bairro = "Bela Vista";
        String cidade = "São Paulo";
        String estado = "SP";
        String cep = "01311000";
        Usuario usuario = new Usuario();


        endereco.setId(id);
        endereco.setLogradouro(logradouro);
        endereco.setNumero(numero);
        endereco.setComplemento(complemento);
        endereco.setBairro(bairro);
        endereco.setCidade(cidade);
        endereco.setEstado(estado);
        endereco.setCep(cep);
        endereco.setUsuario(usuario);


        assertEquals(id, endereco.getId());
        assertEquals(logradouro, endereco.getLogradouro());
        assertEquals(numero, endereco.getNumero());
        assertEquals(complemento, endereco.getComplemento());
        assertEquals(bairro, endereco.getBairro());
        assertEquals(cidade, endereco.getCidade());
        assertEquals(estado, endereco.getEstado());
        assertEquals(cep, endereco.getCep());
        assertEquals(usuario, endereco.getUsuario());
    }

    @Test
    void testAssociacaoUsuario() {

        Endereco endereco = new Endereco();
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");


        endereco.setUsuario(usuario);


        assertEquals(usuario, endereco.getUsuario());
        assertEquals("test@example.com", endereco.getUsuario().getEmail());
    }

    @Test
    void testAtualizacaoDeCampos() {

        Endereco endereco = new Endereco("Rua Antiga", "1", null,
                "Centro", "Rio", "RJ", "20000000", null);


        endereco.setLogradouro("Rua Nova");
        endereco.setNumero("2");
        endereco.setComplemento("Sala 3");
        endereco.setBairro("Copacabana");
        endereco.setCidade("Rio de Janeiro");
        endereco.setEstado("RJ");
        endereco.setCep("22020000");


        assertEquals("Rua Nova", endereco.getLogradouro());
        assertEquals("2", endereco.getNumero());
        assertEquals("Sala 3", endereco.getComplemento());
        assertEquals("Copacabana", endereco.getBairro());
        assertEquals("Rio de Janeiro", endereco.getCidade());
        assertEquals("RJ", endereco.getEstado());
        assertEquals("22020000", endereco.getCep());
    }

    @Test
    void testConstrutorComAlgunsParametrosNulos() {

        Usuario usuario = new Usuario();


        Endereco endereco = new Endereco(null, null, null, null, null, null, null, usuario);


        assertNull(endereco.getLogradouro());
        assertNull(endereco.getNumero());
        assertNull(endereco.getComplemento());
        assertNull(endereco.getBairro());
        assertNull(endereco.getCidade());
        assertNull(endereco.getEstado());
        assertNull(endereco.getCep());
        assertEquals(usuario, endereco.getUsuario());
    }
}