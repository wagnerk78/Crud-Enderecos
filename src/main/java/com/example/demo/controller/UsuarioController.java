package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository,
                             PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ModelAndView listar() {
        ModelAndView modelAndView = new ModelAndView("listar-usuarios");
        modelAndView.addObject("usuarios", usuarioRepository.findAll());
        return modelAndView;
    }

    @GetMapping("/novo")
    public ModelAndView novo() {
        ModelAndView modelAndView = new ModelAndView("form-usuario");
        modelAndView.addObject("usuario", new Usuario());
        return modelAndView;
    }

    @PostMapping
    public String cadastrar(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            redirectAttributes.addFlashAttribute("erroCadastro", "E-mail já existe!");
            return "redirect:/usuarios/";
        }

        usuario.setId(UUID.randomUUID());
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute("mensagemCadastro", "Usuário cadastrado com sucesso!");
        return "redirect:/usuarios/";
    }

    @GetMapping("/{id}/editar")
    public ModelAndView editar(@PathVariable UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        ModelAndView modelAndView = new ModelAndView("form-usuario");
        modelAndView.addObject("usuario", usuario);
        return modelAndView;
    }

    @PostMapping("/{id}")
    public String atualizar(@ModelAttribute Usuario usuario) {
        // Verifica se a senha foi alterada
        if (!usuario.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        } else {
            // Mantém a senha atual se não foi fornecida nova senha
            Usuario usuarioExistente = usuarioRepository.findById(usuario.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
            usuario.setSenha(usuarioExistente.getSenha());
        }

        usuarioRepository.save(usuario);
        redirectAttributes.addFlashAttribute("sucesso", "Usuário atualizado com sucesso");
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/remover")
    public String remover(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if ("admin@admin.com".equals(usuario.getEmail())) {
            redirectAttributes.addFlashAttribute("erro", "Não é possível remover o usuário admin");
            return "redirect:/usuarios";
        }

        usuarioRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("sucesso", "Usuário removido com sucesso");
        return "redirect:/usuarios";
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }
}