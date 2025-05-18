package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
        // Verifica se já existe um usuário com o mesmo e-mail
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            redirectAttributes.addFlashAttribute("erroCadastro", "E-mail já existe!");
            return "redirect:/usuarios/"; // redireciona para o método index
        }

        // Se não existir, salva o novo usuário
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
        usuarioRepository.save(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/remover")
    public String remover(@PathVariable UUID id) {
        usuarioRepository.deleteById(id);
        return "redirect:/usuarios";
    }

    @GetMapping("/")
    public String index(Usuario usuario) {
        return "index";
    }

}

