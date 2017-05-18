package com.algaworks.cobranca.controller;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.algaworks.cobranca.model.Titulo;
import com.algaworks.cobranca.model.StatusTitulo;
import com.algaworks.cobranca.repository.filter.TituloFilter;
import com.algaworks.cobranca.service.CadastroTituloService;

@Controller
@RequestMapping("/titulos")
public class TituloController {

	// Injeção de Dependencia do Spring. Só é possivel porque anotei @Service na
	// class
	@Autowired
	private CadastroTituloService cadastroTituloService;

	// HOME - NOVO CADASTRO
	@RequestMapping("/novo")
	public ModelAndView novo() {
		ModelAndView mv = new ModelAndView("CadastroTitulo");
		mv.addObject(new Titulo());
		return mv;
	}

	// SALVAR
	@RequestMapping(method = RequestMethod.POST)
	public String salvar(@Validated Titulo titulo, Errors errors, RedirectAttributes attributes) {
		if (errors.hasErrors()) {
			return "CadastroTitulo";
		}

		try {
			cadastroTituloService.salvar(titulo);
			// Funcao do controller - preencher dados que a view vai precisar.
			// Regra de negócio deve ser feita na camada Service.
			attributes.addFlashAttribute("mensagem", "Título salvo com sucesso");
			return "redirect:/titulos/novo";
		} catch (IllegalArgumentException e) {
			errors.rejectValue("dataVencimento", "Null", e.getMessage());
			return "CadastroTitulo";
		}

	}

	// PESQUISAR
	@RequestMapping
	public ModelAndView pesquisar(@ModelAttribute("filtro") TituloFilter filtro) {
		List<Titulo> todosTitulos = cadastroTituloService.filtrar(filtro);

		ModelAndView mv = new ModelAndView("PesquisaTitulos");
		// Faz com que a lista "todosTitulos" fique disponível na view com o
		// nome "titulos".
		mv.addObject("titulos", todosTitulos);
		return mv;
	}

	// EDITAR
	// Recebo o parametro vindo do browser ex.: titulos/10
	@RequestMapping("{codigo}")
	public ModelAndView edicao(@PathVariable("codigo") Titulo titulo) {
		ModelAndView mv = new ModelAndView("CadastroTitulo");
		mv.addObject(titulo);
		return mv;
	}

	// EXCLUIR
	@RequestMapping(value = "{codigo}", method = RequestMethod.DELETE)
	public String excluir(@PathVariable Long codigo, RedirectAttributes attributes) {
		cadastroTituloService.excluir(codigo);
		attributes.addFlashAttribute("mensagem", "Título excluído com sucesso");
		return "redirect:/titulos";
	}

	@RequestMapping(value = "/{codigo}/receber", method = RequestMethod.PUT)
	public @ResponseBody String receber(@PathVariable Long codigo) {
		return cadastroTituloService.receber(codigo);

	}

	@ModelAttribute("todosStatusTitulo")
	public List<StatusTitulo> todosStatusTitulo() {
		return Arrays.asList(StatusTitulo.values());
	}
}
