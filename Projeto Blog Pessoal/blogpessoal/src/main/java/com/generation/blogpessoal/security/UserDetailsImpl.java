package com.generation.blogpessoal.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.generation.blogpessoal.model.Usuario;

public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L; 
	// Mensura o tamanho do id do bd ^
	
	private String userName;
	private String password;
	
	// Autoriza todos os privilégios de usuário
	private List <GrantedAuthority> authorities;
	
	// Dá um contexto do que significa o que lá na classe Usuário (Da 'Model')
	public UserDetailsImpl (Usuario usuario) {
		this.userName = usuario.getUsuario();
		this.password = usuario.getSenha();
	}
	
	// Define que todos os usuários terão o mesmo nível de privilégio/acesso (CRUD)
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public String getUsername() {
		return userName;
	}
	
	/* Checa se a conta não está expirada, setado com true 
	 * pra agilizar. É tipo acesso gratuíto aí depois expira */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	// Checa se a conta está bloqueada
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	// Checa se a credencial da conta não está expirada
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	/* Checa sa conta está ativa (Habilitada ou não) 
	 * e se consegue logar com o e-mail e senha */
	@Override
	public boolean isEnabled() {
		return true;
	}
}
