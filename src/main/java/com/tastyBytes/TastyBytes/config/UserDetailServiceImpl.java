package com.tastyBytes.TastyBytes.config;

import com.tastyBytes.TastyBytes.entities.User;
import com.tastyBytes.TastyBytes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user=this.userRepository.findByEmail(username);
        if(user==null){
            throw new UsernameNotFoundException("could not found user");
        }
        CustomUserDetail customUserDetail=new CustomUserDetail(user);
        return customUserDetail;
    }
}
