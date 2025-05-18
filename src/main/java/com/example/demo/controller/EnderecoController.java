package com.example.demo;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EnderecoController {

	@Autowired
	private EnderecoRepository enderecoRepository;

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/contatos")
	public ModelAndView listar() {
		ModelAndView modelAndView = new ModelAndView("listar");
		modelAndView.addObject("contatos", enderecoRepository.findAll());
		return modelAndView;
	}

	@GetMapping("/contatos/novo")
	public ModelAndView novo() {
		ModelAndView modelAndView = new ModelAndView("formulario");
		modelAndView.addObject("contato", new Endereco());
		return modelAndView;
	}

	@PostMapping("/contatos")
	public String cadastrar(Endereco endereco) {
		endereco.setId(UUID.randomUUID().toString());
		enderecoRepository.save(endereco);
		return "redirect:/contatos";
	}

	@GetMapping("/contatos/{id}/editar")
	public ModelAndView editar(@PathVariable String id) {
		ModelAndView modelAndView = new ModelAndView("formulario");
		Endereco endereco = enderecoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Contato não encontrado"));
		modelAndView.addObject("contato", endereco);
		return modelAndView;
	}

	@PutMapping("/contatos/{id}")
	public String atualizar(Endereco endereco) {
		enderecoRepository.save(endereco);
		return "redirect:/contatos";
	}

	@DeleteMapping("/contatos/{id}")
	public String remover(@PathVariable String id) {
		enderecoRepository.deleteById(id);
		return "redirect:/contatos";
	}
}
