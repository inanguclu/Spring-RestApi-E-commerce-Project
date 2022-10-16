package com.works.services;

import com.works.configs.JwtUtil;
import com.works.entities.Admin;
import com.works.entities.User;
import com.works.entities.Role;
import com.works.repositories.AdminRepository;
import com.works.repositories.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuthenticationService implements UserDetailsService {

    final JwtUtil jwtUtil;
    final UserRepository userRepository;
    final AdminRepository adminRepository;
    final AuthenticationManager authenticationManager;
    final HttpSession session;

    public AuthenticationService(JwtUtil jwtUtil, UserRepository userRepository, AdminRepository adminRepository, @Lazy AuthenticationManager authenticationManager, HttpSession session) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.authenticationManager = authenticationManager;
        this.session = session;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalCustomer= userRepository.findByEmailEqualsIgnoreCase(username);

        Optional<Admin> optionalAdmin=adminRepository.findByEmailEqualsIgnoreCase(username);


        if(optionalCustomer.isPresent()&&!optionalAdmin.isPresent()){
            User user =optionalCustomer.get();
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    user.isEnabled(),
                    user.isTokenExpired(),
                    true,
                    true,
                    roles(user.getRoles())

            );

            session.setAttribute("user", user);
            return userDetails;


        }else if(optionalAdmin.isPresent()|| optionalCustomer.isPresent()) {
            Admin admin=optionalAdmin.get();
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    admin.getEmail(),
                    admin.getPassword(),
                    admin.isEnabled(),
                    admin.isTokenExpired(),
                    true,
                    true,
                    roles(admin.getRoles())

            );
            session.setAttribute("admin",admin);
            return userDetails;
        }else {throw new UsernameNotFoundException("There is no such user. ");}

    }
    public Collection roles(List<Role> roles ) {
        List<GrantedAuthority> ls = new ArrayList<>();
        for ( Role role : roles ) {
            ls.add( new SimpleGrantedAuthority( role.getName() ));
        }
        System.out.println(ls);
        return ls;

    }
}
