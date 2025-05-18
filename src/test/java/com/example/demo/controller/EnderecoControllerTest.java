package com.example.demo.controller;

import com.example.demo.model.Endereco;
import com.example.demo.model.Usuario;
import com.example.demo.repository.EnderecoRepository;
import com.example.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EnderecoControllerTest {

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private Principal principal;

    @InjectMocks
    private EnderecoController enderecoController;

    private MockMvc mockMvc;

    private final UUID USER_ID = UUID.randomUUID();
    private final UUID ENDERECO_ID = UUID.randomUUID();
    private final String USER_EMAIL = "user@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(enderecoController).build();

        when(principal.getName()).thenReturn(USER_EMAIL);
    }

    @Test
    void listar_QuandoUsuarioLogado_DeveRetornarEnderecos() {

        Usuario usuario = new Usuario(USER_EMAIL, "password");
        usuario.setId(USER_ID);
        List<Endereco> enderecos = Arrays.asList(new Endereco(), new Endereco());

        when(usuarioRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(usuario));
        when(enderecoRepository.findByUsuario(usuario)).thenReturn(enderecos);


        ModelAndView modelAndView = enderecoController.listar(principal);


        assertEquals("listar-enderecos", modelAndView.getViewName());
        assertEquals(enderecos, modelAndView.getModel().get("enderecos"));
    }

    @Test
    void listar_QuandoUsuarioNaoEncontrado_DeveRedirecionarParaLogin() {

        when(usuarioRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());


        ModelAndView modelAndView = enderecoController.listar(principal);


        assertEquals("redirect:/login", modelAndView.getViewName());
    }

    @Test
    void novo_DeveRetornarFormularioVazio() {

        ModelAndView modelAndView = enderecoController.novo();


        assertEquals("form-endereco", modelAndView.getViewName());
        assertTrue(modelAndView.getModel().get("endereco") instanceof Endereco);
    }

    @Test
    void cadastrar_QuandoUsuarioLogado_DeveSalvarEndereco() {

        Usuario usuario = new Usuario(USER_EMAIL, "password");
        usuario.setId(USER_ID);
        Endereco endereco = new Endereco();

        when(usuarioRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(usuario));


        String result = enderecoController.cadastrar(endereco, principal);


        assertEquals("redirect:/enderecos?sucesso", result);
        verify(enderecoRepository).save(endereco);
        assertEquals(usuario, endereco.getUsuario());
        assertNotNull(endereco.getId());
    }

    @Test
    void cadastrar_QuandoUsuarioNaoEncontrado_DeveRedirecionarComErro() {

        when(usuarioRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());


        String result = enderecoController.cadastrar(new Endereco(), principal);


        assertEquals("redirect:/enderecos?erro", result);
        verify(enderecoRepository, never()).save(any());
    }

    @Test
    void editar_QuandoEnderecoPertenceAoUsuario_DeveRetornarFormulario() {

        Usuario usuario = new Usuario(USER_EMAIL, "password");
        usuario.setId(USER_ID);
        Endereco endereco = new Endereco();
        endereco.setId(ENDERECO_ID);
        endereco.setUsuario(usuario);

        when(enderecoRepository.findById(ENDERECO_ID)).thenReturn(Optional.of(endereco));
        when(usuarioRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(usuario));


        ModelAndView modelAndView = enderecoController.editar(ENDERECO_ID, principal);


        assertEquals("form-endereco", modelAndView.getViewName());
        assertEquals(endereco, modelAndView.getModel().get("endereco"));
    }

    @Test
    void editar_QuandoEnderecoNaoEncontrado_DeveLancarExcecao() {

        when(enderecoRepository.findById(ENDERECO_ID)).thenReturn(Optional.empty());


        assertThrows(IllegalArgumentException.class, () -> {
            enderecoController.editar(ENDERECO_ID, principal);
        });
    }

    @Test
    void editar_QuandoNaoPertenceAoUsuario_DeveRedirecionar() {

        Usuario outroUsuario = new Usuario("other@example.com", "password");
        outroUsuario.setId(UUID.randomUUID());

        Endereco endereco = new Endereco();
        endereco.setId(ENDERECO_ID);
        endereco.setUsuario(outroUsuario);

        Usuario usuarioLogado = new Usuario(USER_EMAIL, "password");
        usuarioLogado.setId(USER_ID);

        when(enderecoRepository.findById(ENDERECO_ID)).thenReturn(Optional.of(endereco));
        when(usuarioRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(usuarioLogado));


        ModelAndView modelAndView = enderecoController.editar(ENDERECO_ID, principal);


        assertEquals("redirect:/enderecos?acessoNegado", modelAndView.getViewName());
    }

    @Test
    void atualizar_QuandoDadosValidos_DeveAtualizarEndereco() {

        Usuario usuario = new Usuario(USER_EMAIL, "password");
        usuario.setId(USER_ID);

        Endereco enderecoOriginal = new Endereco();
        enderecoOriginal.setId(ENDERECO_ID);
        enderecoOriginal.setUsuario(usuario);

        Endereco enderecoAtualizado = new Endereco();
        enderecoAtualizado.setLogradouro("Nova Rua");
        enderecoAtualizado.setNumero("123");
        enderecoAtualizado.setComplemento("Apto 101");
        enderecoAtualizado.setBairro("Centro");
        enderecoAtualizado.setCidade("São Paulo");
        enderecoAtualizado.setEstado("SP");
        enderecoAtualizado.setCep("01001000");

        when(usuarioRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(usuario));
        when(enderecoRepository.findById(ENDERECO_ID)).thenReturn(Optional.of(enderecoOriginal));


        String result = enderecoController.atualizar(ENDERECO_ID, enderecoAtualizado, principal);


        assertEquals("redirect:/enderecos?atualizado", result);
        verify(enderecoRepository).save(enderecoOriginal);
        assertEquals("Nova Rua", enderecoOriginal.getLogradouro());
        assertEquals("123", enderecoOriginal.getNumero());
        assertEquals("Apto 101", enderecoOriginal.getComplemento());
        assertEquals("Centro", enderecoOriginal.getBairro());
        assertEquals("São Paulo", enderecoOriginal.getCidade());
        assertEquals("SP", enderecoOriginal.getEstado());
        assertEquals("01001000", enderecoOriginal.getCep());
    }

    @Test
    void atualizar_QuandoEnderecoNaoPertenceAoUsuario_DeveRedirecionarComAcessoNegado() {

        Usuario outroUsuario = new Usuario("other@example.com", "password");
        outroUsuario.setId(UUID.randomUUID());

        Endereco enderecoOriginal = new Endereco();
        enderecoOriginal.setId(ENDERECO_ID);
        enderecoOriginal.setUsuario(outroUsuario);

        Usuario usuarioLogado = new Usuario(USER_EMAIL, "password");
        usuarioLogado.setId(USER_ID);

        when(usuarioRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(usuarioLogado));
        when(enderecoRepository.findById(ENDERECO_ID)).thenReturn(Optional.of(enderecoOriginal));


        String result = enderecoController.atualizar(ENDERECO_ID, new Endereco(), principal);


        assertEquals("redirect:/enderecos?acessoNegado", result);
        verify(enderecoRepository, never()).save(any());
    }

    @Test
    void remover_QuandoEnderecoPertenceAoUsuario_DeveRemover() {

        Usuario usuario = new Usuario(USER_EMAIL, "password");
        usuario.setId(USER_ID);

        Endereco endereco = new Endereco();
        endereco.setId(ENDERECO_ID);
        endereco.setUsuario(usuario);

        when(enderecoRepository.findById(ENDERECO_ID)).thenReturn(Optional.of(endereco));
        when(usuarioRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(usuario));


        String result = enderecoController.remover(ENDERECO_ID, principal);


        assertEquals("redirect:/enderecos?removido", result);
        verify(enderecoRepository).deleteById(ENDERECO_ID);
    }

    @Test
    void remover_QuandoEnderecoNaoEncontrado_DeveRedirecionarComErro() {

        when(enderecoRepository.findById(ENDERECO_ID)).thenReturn(Optional.empty());


        String result = enderecoController.remover(ENDERECO_ID, principal);


        assertEquals("redirect:/enderecos?erro", result);
        verify(enderecoRepository, never()).deleteById(any());
    }



    @Test
    void listar_GET_DeveRetornarPaginaDeEnderecos() throws Exception {
        Usuario usuario = new Usuario(USER_EMAIL, "password");
        usuario.setId(USER_ID);

        when(usuarioRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(usuario));
        when(enderecoRepository.findByUsuario(usuario)).thenReturn(List.of(new Endereco()));

        mockMvc.perform(get("/enderecos").principal(principal))
                .andExpect(status().isOk())
                .andExpect(view().name("listar-enderecos"))
                .andExpect(model().attributeExists("enderecos"));
    }

    @Test
    void cadastrar_POST_QuandoSucesso_DeveRedirecionar() throws Exception {
        Usuario usuario = new Usuario(USER_EMAIL, "password");
        usuario.setId(USER_ID);

        when(usuarioRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(usuario));

        mockMvc.perform(post("/enderecos")
                        .principal(principal)
                        .param("logradouro", "Rua Teste")
                        .param("numero", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/enderecos?sucesso"));
    }
}