package com.example.demo.controller;

import com.example.demo.model.Endereco;
import com.example.demo.model.Usuario;
import com.example.demo.repository.EnderecoRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/enderecos")
public class EnderecoController {

	@Autowired
	private EnderecoRepository enderecoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@GetMapping
	public ModelAndView listar(Principal principal) {
		String email = principal.getName();
		Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(email);
		if (optionalUsuario.isEmpty()) {
			return new ModelAndView("redirect:/login");
		}

		Usuario usuario = optionalUsuario.get();
		List<Endereco> enderecos = enderecoRepository.findByUsuario(usuario);

		ModelAndView modelAndView = new ModelAndView("listar-enderecos");
		modelAndView.addObject("enderecos", enderecos);
		return modelAndView;
	}

	@GetMapping("/novo")
	public ModelAndView novo() {
		ModelAndView modelAndView = new ModelAndView("form-endereco");
		modelAndView.addObject("endereco", new Endereco());
		return modelAndView;
	}

	@PostMapping
	public String cadastrar(@ModelAttribute Endereco endereco, Principal principal) {
		String email = principal.getName();
		Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(email);

		if (optionalUsuario.isPresent()) {
			Usuario usuario = optionalUsuario.get();
			endereco.setId(UUID.randomUUID());
			endereco.setUsuario(usuario);
			enderecoRepository.save(endereco);
			return "redirect:/enderecos?sucesso";
		}

		return "redirect:/enderecos?erro";
	}

	@GetMapping("/{id}/editar")
	public ModelAndView editar(@PathVariable UUID id, Principal principal) {
		Optional<Endereco> optionalEndereco = enderecoRepository.findById(id);
		if (optionalEndereco.isEmpty()) {
			throw new IllegalArgumentException("Endereço não encontrado");
		}

		Endereco endereco = optionalEndereco.get();

		String email = principal.getName();
		Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(email);
		if (optionalUsuario.isEmpty() || !endereco.getUsuario().getId().equals(optionalUsuario.get().getId())) {
			return new ModelAndView("redirect:/enderecos?acessoNegado");
		}

		ModelAndView modelAndView = new ModelAndView("form-endereco");
		modelAndView.addObject("endereco", endereco);
		return modelAndView;
	}

	@PostMapping("/{id}")
	public String atualizar(@PathVariable UUID id, @ModelAttribute Endereco enderecoAtualizado, Principal principal) {
		String email = principal.getName();
		Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(email);

		if (optionalUsuario.isEmpty()) {
			return "redirect:/login";
		}

		Usuario usuario = optionalUsuario.get();

		Optional<Endereco> optionalEnderecoOriginal = enderecoRepository.findById(id);
		if (optionalEnderecoOriginal.isEmpty()) {
			return "redirect:/enderecos?erro";
		}

		Endereco enderecoOriginal = optionalEnderecoOriginal.get();

		if (!enderecoOriginal.getUsuario().getId().equals(usuario.getId())) {
			return "redirect:/enderecos?acessoNegado";
		}

		enderecoOriginal.setLogradouro(enderecoAtualizado.getLogradouro());
		enderecoOriginal.setNumero(enderecoAtualizado.getNumero());
		enderecoOriginal.setComplemento(enderecoAtualizado.getComplemento());
		enderecoOriginal.setBairro(enderecoAtualizado.getBairro());
		enderecoOriginal.setCidade(enderecoAtualizado.getCidade());
		enderecoOriginal.setEstado(enderecoAtualizado.getEstado());
		enderecoOriginal.setCep(enderecoAtualizado.getCep());

		enderecoRepository.save(enderecoOriginal);

		return "redirect:/enderecos?atualizado";
	}


	@GetMapping("/{id}/remover")
	public String remover(@PathVariable UUID id, Principal principal) {
		Optional<Endereco> optionalEndereco = enderecoRepository.findById(id);
		if (optionalEndereco.isEmpty()) {
			return "redirect:/enderecos?erro";
		}

		Endereco endereco = optionalEndereco.get();

		// Só permite remover se o endereço pertencer ao usuário logado
		String email = principal.getName();
		Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(email);
		if (optionalUsuario.isEmpty() || !endereco.getUsuario().getId().equals(optionalUsuario.get().getId())) {
			return "redirect:/enderecos?acessoNegado";
		}

		enderecoRepository.deleteById(id);
		return "redirect:/enderecos?removido";
	}
}
