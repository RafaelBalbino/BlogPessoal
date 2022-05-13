package com.generation.blogpessoal.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Notação que diz ao Spring que isso é 'Security'
@EnableWebSecurity
public class BasicSecurityConfig extends WebSecurityConfigurerAdapter {
	
	/* Serve para validar os dados digitados, comparando-os com os dados 
	salvos do banco de dados, que a UserDetailsService cuida. */
	@Autowired
	private UserDetailsService userDetailsService;
	
	/* Usuário em memória para teste - Checa no banco de dados, não acha 
	 * aí tem a exceção em baixo (Um usuário para testar a API mais rápido), sem 
	 * validação de token pra agilizar testes no processo de desenvolvimento. */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService); // Usa a superclasse 'userDetailsService'
		// Sem ela você não consegue cadastrar nenhum usuário, só vai dar pra usar o que tá em memória
		
		auth.inMemoryAuthentication()
		.withUser("root") // Checa se o usuário é 'root', aí pula pra checagem da senha
		.password(passwordEncoder().encode("root")) /* Checa se a senha é 'root' e encripta ela
													 * também gerando um token. */
		                                            /* Precisa de uma dependência, que já está 
		                                             * no pom.xml (Spring Boot Starter Security) */
		.authorities("ROLE_USER"); /* Valida que é um usuário. Necessário para saber que tipo
								  * de usuário que é também. Se não tiver, não terá permissões 
								  * de CRUD de um usuário normal e válido. */
	}
	
	// Notação que deixa uma função acessível globalmente (Em toda a aplicação)
	@Bean
	
	// Função que encripta a senha digitada, e nunca muda como é escrita essa função
	public PasswordEncoder passwordEncoder() { 
		return new BCryptPasswordEncoder();
	}
	
	/* Para codar o que o usuário pode fazer quando não está logado e quando está, deixando 
	 * uma rota aberta pra poder pelo menos poder cadastrar uma conta, tipo na primeira vez 
	 * que você acessa o Facebook e tem a página de cadastro/login, que é aberta ao público. */
	// Nesse caso, duas rotas estão acessíveis: Login e Cadastro
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http.authorizeRequests()
		.antMatchers("/usuario/logar").permitAll() // De qualquer lugar, você terá acesso a login
		.antMatchers("/usuario/cadastrar").permitAll() // e cadastro já que as rotas estão abertas
		.antMatchers(HttpMethod.OPTIONS).permitAll() /* Permite que as rotas estejam acessíveis com GET
		 											  * Permite saber quais métodos estão abertos na
		 											  * documentação da API e que estão abertos nela 
		 											  * e é possível utilizar eles. */
		.anyRequest().authenticated() // Para outras requisições, tem que está ou cadastrado ou em memória
		.and().httpBasic() // HttpBasic = CRUD | Define que só será aceito métodos CRUD
		.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) 
		/* ^ Define que toda requisição tem começo, meio e fim. Uma por vez e ajuda a prevenir ataques 
		 * cibernéticos e invasões com várias requisições de uma forma | Tipo quando expira o token 
		 * em um site como na plataforma da Generation Brasil. ^ */
		.and().cors() /* <-- Funciona como o '@CrossOrigins', vendo de qual porta está vindo a requisição
		               * e liberando acesso para todas (Do Front-end pro Back-end) basicamente. */
		.and().csrf().disable(); // Autoriza PUT e DELETE nas requisições.
	}
}
