package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UsuarioControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioController usuarioController;

    private MockMvc mockMvc;

    @Mock
    private RedirectAttributes redirectAttributes;

    private final UUID USER_ID = UUID.randomUUID();
    private final String USER_EMAIL = "user@example.com";
    private final String ADMIN_EMAIL = "admin@admin.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
        redirectAttributes = new RedirectAttributesModelMap();
    }

    @Test
    void listar_DeveRetornarListaDeUsuarios() {

        Usuario usuario1 = new Usuario(USER_EMAIL, "senha123");
        Usuario usuario2 = new Usuario("outro@email.com", "senha456");
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario1, usuario2));


        ModelAndView modelAndView = usuarioController.listar();


        assertEquals("listar-usuarios", modelAndView.getViewName());
        assertEquals(2, ((List<?>) modelAndView.getModel().get("usuarios")).size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void novo_DeveRetornarFormularioVazio() {

        ModelAndView modelAndView = usuarioController.novo();


        assertEquals("form-usuario", modelAndView.getViewName());
        assertNotNull(modelAndView.getModel().get("usuario"));
        assertTrue(modelAndView.getModel().get("usuario") instanceof Usuario);
    }


    @Test
    void editar_QuandoUsuarioExiste_DeveRetornarFormularioPreenchido() {

        Usuario usuario = new Usuario(USER_EMAIL, "senha123");
        usuario.setId(USER_ID);
        when(usuarioRepository.findById(USER_ID)).thenReturn(Optional.of(usuario));


        ModelAndView modelAndView = usuarioController.editar(USER_ID);


        assertEquals("form-usuario", modelAndView.getViewName());
        assertEquals(usuario, modelAndView.getModel().get("usuario"));
    }

    @Test
    void editar_QuandoUsuarioNaoExiste_DeveLancarExcecao() {

        when(usuarioRepository.findById(USER_ID)).thenReturn(Optional.empty());


        assertThrows(IllegalArgumentException.class, () -> {
            usuarioController.editar(USER_ID);
        });
    }

    @Test
    void atualizar_DeveSalvarUsuarioERedirecionar() {

        Usuario usuario = new Usuario(USER_EMAIL, "senha123");
        usuario.setId(USER_ID);

        String result = usuarioController.atualizar(usuario);

        assertEquals("redirect:/usuarios", result);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void remover_QuandoUsuarioNaoEAdmin_DeveRemover() {

        Usuario usuario = new Usuario(USER_EMAIL, "senha123");
        usuario.setId(USER_ID);
        when(usuarioRepository.findById(USER_ID)).thenReturn(Optional.of(usuario));

        String result = usuarioController.remover(USER_ID, redirectAttributes);

        assertEquals("redirect:/usuarios", result);
        verify(usuarioRepository).deleteById(USER_ID);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("sucesso"));
    }

    @Test
    void remover_QuandoUsuarioNaoExiste_DeveLancarExcecao() {

        when(usuarioRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioController.remover(USER_ID, redirectAttributes);
        });
    }

    @Test
    void index_DeveRetornarPaginaIndex() {

        String result = usuarioController.index();

        assertEquals("index", result);
    }


    @Test
    void listar_GET_DeveRetornarPaginaDeUsuarios() throws Exception {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(new Usuario(), new Usuario()));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("listar-usuarios"))
                .andExpect(model().attributeExists("usuarios"));
    }

    @Test
    void cadastrar_POST_QuandoSucesso_DeveRedirecionar() throws Exception {
        when(usuarioRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashSenha");

        mockMvc.perform(post("/usuarios")
                        .param("email", USER_EMAIL)
                        .param("senha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/"))
                .andExpect(flash().attributeExists("mensagemCadastro"));
    }

}