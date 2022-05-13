package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest (webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
/*  indica em qual ordem os testes serão executados. 
 * A opção MethodOrderer.OrderAnnotaion.class indica 
 * que os testes serão executados na ordem indicada 
 * pela anotação @Order inserida em cada teste. */
public class UsuarioControllerTest {
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Test
	@Order(1)
	@DisplayName("Cadastrar um usuário")
	public void deveCriarUmUsuario() {
		HttpEntity <Usuario> requisicao = new HttpEntity <Usuario> (new Usuario(0L, 
				"Maiar", "maiar@gmail.com", "51 nao e pinga puxa vida", "https://i.imgur.com/FETvs2O.jpg"));
		
		ResponseEntity <Usuario> resposta = testRestTemplate
				.exchange("/usuario/cadastrar", HttpMethod.POST, requisicao, Usuario.class);
		
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		assertEquals(requisicao.getBody().getNome(), resposta.getBody().getNome());
		assertEquals(requisicao.getBody().getUsuario(), resposta.getBody().getUsuario());
	}
	
	@Test
	@Order(2)
	@DisplayName("Não deve permitir duplicação de usuário")
	public void naoDeveDuplicarUmUsuario() {
		
		usuarioService.cadastraUsuario(new Usuario(0L, 
				"Charlie Brown", "jigjey@gmail.com", "skate", "https://i.imgur.com/FETvs2O.jpg"));
		
		HttpEntity <Usuario> requisicao = new HttpEntity <Usuario> (new Usuario(0L, 
				"Charlie Brown", "jigjey@gmail.com", "skate", "https://i.imgur.com/FETvs2O.jpg"));
		
		ResponseEntity <Usuario> resposta = testRestTemplate
				.exchange("/usuario/cadastrar", HttpMethod.POST, requisicao, Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
	}
	
	@Test
	@Order(3)
	@DisplayName("Atualizar um usuário")
	public void deveAtualizarUmUsuario() {
		
		Optional <Usuario> usuarioCreate = usuarioService.cadastraUsuario(new Usuario(0L, 
				"Maria Aparecidade", "chanimani@gmail.com", "rafitcha", "https://i.imgur.com/FETvs2O.jpg"));
		
		Usuario usuarioUpdate = new Usuario(usuarioCreate.get().getId(),"Maria Aparecida", 
				     "xanimani@gmail.com", "rafitcha", "https://i.imgur.com/FETvs2O.jpg");
		
		HttpEntity <Usuario> requisicao = new HttpEntity <Usuario> (usuarioUpdate);
	
		ResponseEntity <Usuario> resposta = testRestTemplate
				.withBasicAuth("root", "root")
				.exchange("/usuario/atualizar", HttpMethod.PUT, requisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertEquals(usuarioUpdate.getNome(), resposta.getBody().getNome());
		assertEquals(usuarioUpdate.getUsuario(), resposta.getBody().getUsuario());
	}
	
	@Test
	@Order(4)
	@DisplayName("Listar todos os usuários")
	public void deveMostrarTodosOsUsuarios() {
		
		usuarioService.cadastraUsuario(new Usuario(0L, "Pica-pau", "picapau@gmail.com", "leoncio", 
																"https://i.imgur.com/FETvs2O.jpg"));
		
		usuarioService.cadastraUsuario(new Usuario(0L, "Leôncio", "leoncio@gmail.com", "almondega", 
														         "https://i.imgur.com/FETvs2O.jpg"));
		ResponseEntity <String> resposta = testRestTemplate
				.withBasicAuth("root", "root")
				.exchange("/usuario/all", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
}