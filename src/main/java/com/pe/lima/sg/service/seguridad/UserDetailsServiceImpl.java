package com.pe.lima.sg.service.seguridad;

import com.pe.lima.sg.dao.seguridad.IUsuarioDAO;
import com.pe.lima.sg.entity.seguridad.TblUsuario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	@Autowired
	private IUsuarioDAO usuarioDao;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	LOGGER.debug("[loadUserByUsername] inicio");
    	LOGGER.debug("[loadUserByUsername] username:"+username);
        TblUsuario user = usuarioDao.findOneByLogin(username);
        LOGGER.debug("[loadUserByUsername] clave:"+user.getClave());
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        //for (Role role : user.getRoles()){
            grantedAuthorities.add(new SimpleGrantedAuthority("ADMIN"));
            grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
            grantedAuthorities.add(new SimpleGrantedAuthority("INVITADO"));
        //}
            LOGGER.debug("[loadUserByUsername] Fin");
        //return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getClave(), grantedAuthorities);
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getLogin(), user.getClave(), grantedAuthorities);
             return userDetails;
    }
}