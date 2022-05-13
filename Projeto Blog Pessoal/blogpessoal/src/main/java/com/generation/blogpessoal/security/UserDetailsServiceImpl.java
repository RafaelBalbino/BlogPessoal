package com.generation.blogpessoal.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private UsuarioRepository userRepository;
	
	// 'String userName' seria o parâmetro e-mail, como 'rafaelballabi@hotmail.com'
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException{
		Optional<Usuario> usuario = userRepository.findByUsuario(userName);
		usuario.orElseThrow(() -> new UsernameNotFoundException(userName + "Usuário não encontrado | " + 
		/* ^ Só conseguimos usar ele por causa do Optional (O 'throws') */ "User not found")); 
		/* Se não encontrar, para por aqui. */
		
		return usuario.map(UserDetailsImpl :: new).get(); 
		/* Se encontrar o usuário, vai passar pela validação | Atribui o resultado encontrado no Optional
		 * para alimentar o UserDetails. */
	}
}
