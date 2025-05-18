package com.example.demo.controller;

import com.example.demo.model.Endereco;
import com.example.demo.model.Usuario;
import com.example.demo.repository.EnderecoRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @GetMapping("/usuarios")
    public ModelAndView listarUsuarios() {
        ModelAndView modelAndView = new ModelAndView("admin/listar-usuarios");
        modelAndView.addObject("usuarios", usuarioRepository.findAll());
        return modelAndView;
    }

    @GetMapping("/enderecos")
    public ModelAndView listarTodosEnderecos() {
        ModelAndView modelAndView = new ModelAndView("admin/listar-todos-enderecos");
        modelAndView.addObject("enderecos", enderecoRepository.findAll());
        return modelAndView;
    }

    @GetMapping("/usuarios/{id}/remover")
    @Transactional
    public String removerUsuario(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            if ("admin@admin.com".equals(usuario.getEmail())) {
                redirectAttributes.addFlashAttribute("erro", "Não é possível remover o usuário admin");
                return "redirect:/admin/usuarios";
            }
            enderecoRepository.deleteByUsuarioId(id);

            usuarioRepository.deleteById(id);

            redirectAttributes.addFlashAttribute("sucesso", "Usuário e seus endereços foram removidos com sucesso");
            return "redirect:/admin/usuarios";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao remover usuário: " + e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    @GetMapping("/usuarios/{id}/enderecos")
    public ModelAndView listarEnderecosDoUsuario(@PathVariable UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        List<Endereco> enderecos = enderecoRepository.findByUsuario(usuario);

        ModelAndView modelAndView = new ModelAndView("admin/listar-enderecos-usuario");
        modelAndView.addObject("enderecos", enderecos);
        modelAndView.addObject("usuario", usuario);
        return modelAndView;
    }

    @GetMapping("/enderecos/{id}/editar")
    public ModelAndView editarEndereco(@PathVariable UUID id) {
        Optional<Endereco> optionalEndereco = enderecoRepository.findById(id);
        if (optionalEndereco.isEmpty()) {
            throw new IllegalArgumentException("Endereço não encontrado");
        }

        Endereco endereco = optionalEndereco.get();
        ModelAndView modelAndView = new ModelAndView("admin/form-endereco");
        modelAndView.addObject("endereco", endereco);
        return modelAndView;
    }

    @PostMapping("/enderecos/{id}")
    public String atualizarEndereco(@PathVariable UUID id, @ModelAttribute Endereco enderecoAtualizado) {
        Optional<Endereco> optionalEndereco = enderecoRepository.findById(id);
        if (optionalEndereco.isEmpty()) {
            return "redirect:/admin/enderecos?erro";
        }

        Endereco endereco = optionalEndereco.get();

        endereco.setLogradouro(enderecoAtualizado.getLogradouro());
        endereco.setNumero(enderecoAtualizado.getNumero());
        endereco.setComplemento(enderecoAtualizado.getComplemento());
        endereco.setBairro(enderecoAtualizado.getBairro());
        endereco.setCidade(enderecoAtualizado.getCidade());
        endereco.setEstado(enderecoAtualizado.getEstado());
        endereco.setCep(enderecoAtualizado.getCep());

        enderecoRepository.save(endereco);

        return "redirect:/admin/usuarios/" + endereco.getUsuario().getId() + "/enderecos?atualizado";
    }

    @GetMapping("/enderecos/{id}/remover")
    @Transactional
    public String removerEndereco(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            Endereco endereco = enderecoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado"));

            UUID usuarioId = endereco.getUsuario().getId();
            enderecoRepository.delete(endereco);

            redirectAttributes.addAttribute("removido", "true");
            return "redirect:/admin/usuarios/" + usuarioId + "/enderecos";

        } catch (Exception e) {
            redirectAttributes.addAttribute("erro", "true");
            return "redirect:/admin/enderecos";
        }
    }


    @GetMapping("/")
    public String mostrarLogin() {
        return "index"; // Nome do template login.html
    }

}
