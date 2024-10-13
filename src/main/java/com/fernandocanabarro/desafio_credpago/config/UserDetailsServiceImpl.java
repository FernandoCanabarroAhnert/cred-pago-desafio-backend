package com.fernandocanabarro.desafio_credpago.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fernandocanabarro.desafio_credpago.entities.Role;
import com.fernandocanabarro.desafio_credpago.entities.User;
import com.fernandocanabarro.desafio_credpago.projections.UserDetailsProjection;
import com.fernandocanabarro.desafio_credpago.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired
    private UserRepository  userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> list = userRepository.searchUserAndRolesByUsername(username);
        if (list.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        User user = new User();
        user.setEmail(list.get(0).getUsername());
        user.setPassword(list.get(0).getPassword());
        for (UserDetailsProjection x : list){
            user.addRole(new Role(x.getRoleId(), x.getAuthority()));
        }
        return user;
    }

}
