package com.generation.blogpessoal.service;

import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;

@Service
public class UsuarioService {
	
	@Autowired
	private UsuarioRepository repository;
	
	// Função para cadastrar um usuário
	/* Lembra de colocar um 'return' após 'if' ou será necessário um 
	 * ';' depois de '.isPresent())', pois o Java acha que o primeiro 
	 * 'return' é considerado o da função. */
	public Optional <Usuario> cadastraUsuario(Usuario usuario) {
		
		// Checa se o usuário já existe no banco de dados antes de fazer o cadastro 
		if (repository.findByUsuario(usuario.getUsuario()).isPresent())
			return Optional.empty();
		
		// Encriptografa a senha do usuário caso não exista 
		/* Sem essa linha, uma senha '1234' será salva sem a criptografia do jeito 
		que foi escrita pelo usuário */
		usuario.setSenha(criptografarSenha(usuario.getSenha()));
		
		// Salva o usuário com senha já criptografada no banco de dados pelo 'repository'
		// Esse retorno (salvamento) tem que ser escrito depois da linha acima
		return Optional.of(repository.save(usuario));
	}
	
	public Optional <Usuario> atualizarUsuario(Usuario usuario) {
		
		// Checa se o id já existe no banco de dados antes de fazer a atualização 
		if (repository.findById(usuario.getId()).isPresent()) {
			/* Senão criptografar de novo, vai mandar ela sem a criptografia aí
			 * vai dar erro por que o sistema não entende, não estará no padrão 
			 * necessário. */
			usuario.setSenha(criptografarSenha(usuario.getSenha()));
			
			// Aí salva a senha criptografada
			return Optional.of(repository.save(usuario));
		}
			
		return Optional.empty();	
		// Salva o usuário com senha já criptografada no banco de dados pelo 'repository'
	}

	// Função que encriptografa a senha antes do cadastro ser completado
	private String criptografarSenha(String senha) {
		
		// Responsável por encriptografar a senha
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		// Função que retorna a senha criptografada, com o parâmetro 'senha' e função 'encode'
		return encoder.encode(senha);
	}
	
	// Função que autentica/faz login (d)o usuário
	// Usa 'UsuarioLogin' por que estamos utilizando o 'Token' que só tem lá
	public Optional <UsuarioLogin> autenticaUsuario(Optional <UsuarioLogin> usuarioLogin) {
		// Busca no banco de dados o usuário pra ver se ele é igual ao digitado
		Optional <Usuario> usuario = repository.findByUsuario(usuarioLogin.get().getUsuario());
		
		if (usuario.isPresent()) {
			if(compararSenhas(usuarioLogin.get().getSenha(), usuario.get().getSenha())) {
				usuarioLogin.get().setId(usuario.get().getId()); /* O .get() é para pegar os atributos relevantes do banco */
				usuarioLogin.get().setNome(usuario.get().getNome());             /* de dados para alimentar 'usuarioLogin' */
				usuarioLogin.get().setFoto(usuario.get().getFoto());
				usuarioLogin.get().setToken(gerarBasicToken(usuarioLogin.get().getUsuario(), usuarioLogin.get().getSenha()));
				usuarioLogin.get().setSenha(usuario.get().getSenha());
				/* Inserimos no 'usuarioLogin' e tudo isso é comparado ao que já foi cadastrado em 'usuario', passando o que 
				 * foi cadastrado no banco de dados (No 'usuario') para o objeto 'usuarioLogin' */
				// Basicamente tudo o que achar no banco de dados é construído no objeto 'usuarioLogin' (Alimentação)
				/* Isso é feito já que só inserimos o 'usuario' e 'senha', aí completa o objeto por inteiro com os outros 
				 * atributos, como foto */
				// Por isso que dá uma travada quando logamos em alguma rede social como Facebook ou Instagram
				
				return usuarioLogin; // Aí retorna o objeto completo com todas as informações
			}
		}
		return Optional.empty(); /* Retorna uma das opções do Optional, que é 'vazio'. Não existe dentro do 'List'
								  * É como se fosse um status vazio */
	}
	
	// Compara se a senha digitada ('usuarioLogin') é a mesma que está no banco de dados ('usuario')
	private boolean compararSenhas(String senhaDigitada, String senhaBanco) {	
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		// Vai encriptografar a senha inserida, aí checa lá no banco de dados se é igual
		// Se for igual, retorna 'true' e loga o usuário, senão retorna 'false' e não deixa logar
		// 'matches' é a função matemática de comparação como se fosse a biblioteca 'Math' do JAVA
		return encoder.matches(senhaDigitada, senhaBanco);
	}
	
	// Função que gera o token, que é a chave validada quando o usuário logar
	private String gerarBasicToken(String usuario, String senha) {

		String token = usuario + ":" + senha; // Como o token deve ser montado 
		byte[] tokenBase64 = Base64.encodeBase64(token.getBytes(Charset.forName("US-ASCII")));
		// Mapeia o que você digitar, converte para binário(ASCII) aí encripta para Base64
		// 'import org.apache.commons.codec.binary.Base64; ' <- Este é o import correto,
		// Depois 'import java.nio.charset.Charset'
		
		return "Basic " + new String(tokenBase64); /* Pega a String criada na linha de cima e faça a junção
		 											* com 'Basic' para montar o token básico por completo. */
	}
}