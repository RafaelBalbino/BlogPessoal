package com.generation.blogpessoal.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.generation.blogpessoal.model.Usuario;

/* Indica que é uma classe de teste que vai rodar em uma porta aleatória
 * em cada teste realizado. */
@SpringBootTest (webEnvironment = WebEnvironment.RANDOM_PORT)

/* Cria uma instância de testes e define que o ciclo de vida do teste vai 
 * respeitar o ciclo de vida da classe (será executado e resetado após o uso)
 * Limpa o ambiente para o próximo teste da classe, poupando processamento. */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioRepositoryTest {
	
	@Autowired 
	private UsuarioRepository repository;
	
	@BeforeAll
	void start() {
		// Tem que ter os construtores em 'Usuario' caso dê erro, sempre na ordem dos atributos
		// 'id' que não é valido e não vamos usar, considerado como padrão(0L) mas é necessário | Tipo 'Long'
		// 'nome' do usuário
		// 'usuario(e-mail)' do usuário
		// 'foto' genérica do usuário
		repository.save(new Usuario(0L, "Maiar da Silva", "isadora@gmail.com", "51 e pega", "https://i.imgur.com/FETvs2O.jpg"));
		repository.save(new Usuario(0L, "Michael da Silva", "michaeltrimundial@gmail.com", "nunca foi rebaixado", "https://i.imgur.com/FETvs2O.jpg"));
		repository.save(new Usuario(0L, "Brocco da Silva", "brocco@gmail.com", "broccolis", "https://i.imgur.com/FETvs2O.jpg"));
		repository.save(new Usuario(0L, "Mayara dos Santos", "will31smith@gmail.com", "cenourinha", "https://i.imgur.com/FETvs2O.jpg"));
	}
	
	@Test // Diz ao Spring que essa é uma função para testes
	@DisplayName("Teste que retorna 1 usuario") // Nome do teste
	public void retornaUmUsuario() { // Toda função que tem um 'public' na frente significa que estamos criando ela
		Optional <Usuario> usuario = repository.findByUsuario("isadora@gmail.com"); // Pega a função de pegar 'usuario' lá do repository
		assertTrue(usuario.get().getUsuario().equals("isadora@gmail.com")); 
		// Retorna um boolean ('true' ou 'false')
		// Faz uma comparação do 'usuario'(e-mail) acima com a resposta que o sistema der. Ela tem que ser igual para dar certo.
		// Se retornar 'true' é por que trouxe o que passamos e se retornar 'false' é por que não encontou
	}
	
	@Test // Diz ao Spring que essa é uma função para testes
	@DisplayName("Teste que retorna 3 usuários") // Nome do teste
	public void retornaTresUsuario() { // Toda função que tem um 'public' na frente significa que estamos criando ela
		List <Usuario> listaDeUsuarios = repository.findAllByNomeContainingIgnoreCase("Silva");
		assertEquals(3, listaDeUsuarios.size()); 
		assertTrue(listaDeUsuarios.get(0).getNome().equals("Maiar da Silva")); 
		assertTrue(listaDeUsuarios.get(1).getNome().equals("Michael da Silva")); 
		assertTrue(listaDeUsuarios.get(2).getNome().equals("Brocco da Silva")); 
	}
	
	@AfterAll
	public void end() {
		repository.deleteAll();
	}
}