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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EnderecoRepository enderecoRepository;

    @InjectMocks
    private AdminController adminController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    void listarUsuarios_DeveRetornarViewComListaDeUsuarios() {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(
                new Usuario("user1@example.com", "senha123"),
                new Usuario("user2@example.com", "senha456")
        );
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // Act
        ModelAndView modelAndView = adminController.listarUsuarios();

        // Assert
        assertEquals("admin/listar-usuarios", modelAndView.getViewName());
        assertEquals(usuarios, modelAndView.getModel().get("usuarios"));
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void listarTodosEnderecos_DeveRetornarViewComListaDeEnderecos() {
        // Arrange
        List<Endereco> enderecos = Arrays.asList(
                new Endereco(),
                new Endereco()
        );
        when(enderecoRepository.findAll()).thenReturn(enderecos);

        // Act
        ModelAndView modelAndView = adminController.listarTodosEnderecos();

        // Assert
        assertEquals("admin/listar-todos-enderecos", modelAndView.getViewName());
        assertEquals(enderecos, modelAndView.getModel().get("enderecos"));
        verify(enderecoRepository, times(1)).findAll();
    }

    @Test
    void removerUsuario_QuandoUsuarioNaoEAdmin_DeveRemoverUsuarioEEnderecos() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Usuario usuario = new Usuario("user@example.com", "senha123");
        usuario.setId(userId);

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        // Act
        String result = adminController.removerUsuario(userId, redirectAttributes);

        // Assert
        assertEquals("redirect:/admin/usuarios", result);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("sucesso"));
        verify(enderecoRepository, times(1)).deleteByUsuarioId(userId);
        verify(usuarioRepository, times(1)).deleteById(userId);
    }

    @Test
    void removerUsuario_QuandoUsuarioEAdmin_DeveRetornarErro() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Usuario usuario = new Usuario("admin@admin.com", "senha123");
        usuario.setId(userId);

        // Garanta que o email está exatamente como esperado (incluindo case sensitivity)
        usuario.setEmail("admin@admin.com"); // Força o email exato

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        // Act
        String result = adminController.removerUsuario(userId, redirectAttributes);

        // Assert
        assertEquals("redirect:/admin/usuarios", result);

        // Verificação mais detalhada do erro
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("erro"),
                "O atributo flash 'erro' deveria estar presente");
        assertEquals("Não é possível remover o usuário admin",
                redirectAttributes.getFlashAttributes().get("erro"));

        verify(enderecoRepository, never()).deleteByUsuarioId(userId);
        verify(usuarioRepository, never()).deleteById(userId);
    }

    @Test
    void removerUsuario_QuandoUsuarioNaoExiste_DeveRetornarErro() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        // Act
        String result = adminController.removerUsuario(userId, redirectAttributes);

        // Assert
        assertEquals("redirect:/admin/usuarios", result);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("erro"));
        verify(enderecoRepository, never()).deleteByUsuarioId(userId);
        verify(usuarioRepository, never()).deleteById(userId);
    }

    @Test
    void listarEnderecosDoUsuario_DeveRetornarViewComEnderecosDoUsuario() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Usuario usuario = new Usuario("user@example.com", "senha123");
        usuario.setId(userId);

        List<Endereco> enderecos = Arrays.asList(
                new Endereco(),
                new Endereco()
        );

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));
        when(enderecoRepository.findByUsuario(usuario)).thenReturn(enderecos);

        // Act
        ModelAndView modelAndView = adminController.listarEnderecosDoUsuario(userId);

        // Assert
        assertEquals("admin/listar-enderecos-usuario", modelAndView.getViewName());
        assertEquals(enderecos, modelAndView.getModel().get("enderecos"));
        assertEquals(usuario, modelAndView.getModel().get("usuario"));
    }

    @Test
    void listarEnderecosDoUsuario_QuandoUsuarioNaoExiste_DeveLancarExcecao() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            adminController.listarEnderecosDoUsuario(userId);
        });
    }

    @Test
    void editarEndereco_DeveRetornarViewComEndereco() {
        // Arrange
        UUID enderecoId = UUID.randomUUID();
        Endereco endereco = new Endereco();
        when(enderecoRepository.findById(enderecoId)).thenReturn(Optional.of(endereco));

        // Act
        ModelAndView modelAndView = adminController.editarEndereco(enderecoId);

        // Assert
        assertEquals("admin/form-endereco", modelAndView.getViewName());
        assertEquals(endereco, modelAndView.getModel().get("endereco"));
    }

    @Test
    void editarEndereco_QuandoEnderecoNaoExiste_DeveLancarExcecao() {
        // Arrange
        UUID enderecoId = UUID.randomUUID();
        when(enderecoRepository.findById(enderecoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            adminController.editarEndereco(enderecoId);
        });
    }

    @Test
    void atualizarEndereco_DeveAtualizarERedirecionar() {
        // Arrange
        UUID enderecoId = UUID.randomUUID();
        Endereco enderecoExistente = new Endereco();
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        enderecoExistente.setUsuario(usuario);

        Endereco enderecoAtualizado = new Endereco();
        enderecoAtualizado.setLogradouro("Nova Rua");
        enderecoAtualizado.setNumero("123");
        enderecoAtualizado.setComplemento("Apto 101");
        enderecoAtualizado.setBairro("Centro");
        enderecoAtualizado.setCidade("São Paulo");
        enderecoAtualizado.setEstado("SP");
        enderecoAtualizado.setCep("01001000");

        when(enderecoRepository.findById(enderecoId)).thenReturn(Optional.of(enderecoExistente));

        // Act
        String result = adminController.atualizarEndereco(enderecoId, enderecoAtualizado);

        // Assert
        assertEquals("redirect:/admin/usuarios/" + usuario.getId() + "/enderecos?atualizado", result);
        verify(enderecoRepository, times(1)).save(enderecoExistente);
        assertEquals("Nova Rua", enderecoExistente.getLogradouro());
        assertEquals("123", enderecoExistente.getNumero());
        assertEquals("Apto 101", enderecoExistente.getComplemento());
        assertEquals("Centro", enderecoExistente.getBairro());
        assertEquals("São Paulo", enderecoExistente.getCidade());
        assertEquals("SP", enderecoExistente.getEstado());
        assertEquals("01001000", enderecoExistente.getCep());
    }

    @Test
    void atualizarEndereco_QuandoEnderecoNaoExiste_DeveRedirecionarComErro() {
        // Arrange
        UUID enderecoId = UUID.randomUUID();
        when(enderecoRepository.findById(enderecoId)).thenReturn(Optional.empty());

        // Act
        String result = adminController.atualizarEndereco(enderecoId, new Endereco());

        // Assert
        assertEquals("redirect:/admin/enderecos?erro", result);
        verify(enderecoRepository, never()).save(any());
    }

    @Test
    void removerEndereco_DeveRemoverERedirecionar() {
        // Arrange
        UUID enderecoId = UUID.randomUUID();
        Endereco endereco = new Endereco();
        Usuario usuario = new Usuario();
        UUID usuarioId = UUID.randomUUID();
        usuario.setId(usuarioId);
        endereco.setUsuario(usuario);

        when(enderecoRepository.findById(enderecoId)).thenReturn(Optional.of(endereco));
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        // Act
        String result = adminController.removerEndereco(enderecoId, redirectAttributes);

        // Assert - Verifica apenas o caminho base do redirecionamento
        assertTrue(result.startsWith("redirect:/admin/usuarios/" + usuarioId + "/enderecos"));

        // Verifica se o atributo foi adicionado
        assertTrue(redirectAttributes.getAttribute("removido") != null);
        assertEquals("true", redirectAttributes.getAttribute("removido"));

        verify(enderecoRepository, times(1)).delete(endereco);
    }

    @Test
    void mostrarLogin_DeveRetornarViewDeLogin() {
        // Act
        String result = adminController.mostrarLogin();

        // Assert
        assertEquals("index", result);
    }

    // Testes de integração com MockMvc para verificar o comportamento HTTP

    @Test
    void listarUsuarios_GET_DeveRetornarStatus200() throws Exception {
        when(usuarioRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/admin/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/listar-usuarios"));
    }

    @Test
    void removerUsuario_GET_QuandoSucesso_DeveRedirecionarComFlashAttribute() throws Exception {
        UUID userId = UUID.randomUUID();
        Usuario usuario = new Usuario("user@example.com", "senha123");
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/admin/usuarios/{id}/remover", userId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/usuarios"))
                .andExpect(flash().attributeExists("sucesso"));
    }
}