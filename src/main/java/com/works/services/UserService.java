package com.works.services;

import com.works.entities.User;
import com.works.repositories.UserRepository;
import com.works.utils.REnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class UserService {
    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;
    final HttpSession session;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, HttpSession session) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.session = session;
    }

    public ResponseEntity register(User user) {
        Optional<User> optionalJWTUser = userRepository.findByEmailEqualsIgnoreCase(user.getEmail());
        Map<REnum, Object> hm = new LinkedHashMap();
        if (!optionalJWTUser.isPresent()) {
            user.setFirstName(user.getFirstName());
            user.setLastName(user.getLastName());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            hm.put(REnum.status, true);
            hm.put(REnum.result, user);
            return new ResponseEntity(hm, HttpStatus.OK);
        } else {
            hm.put(REnum.status, false);
            hm.put(REnum.message, "This e-mail is already registered");
            hm.put(REnum.result, user);
            return new ResponseEntity(hm, HttpStatus.NOT_ACCEPTABLE);
        }


    }

    public ResponseEntity changePassword(String oldPassword, String newPassword) {
        Map<REnum, Object> hm = new LinkedHashMap();
       Object object=session.getAttribute("user");
       if(object!=null){
        User user = (User) object;

        if (this.passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            User updatedUser = userRepository.save(user);
            hm.put(REnum.status, "true");
            hm.put(REnum.result, updatedUser);
            return new ResponseEntity<>(hm, HttpStatus.OK);
        } else {
            hm.put(REnum.message, "Please check again current password");
            hm.put(REnum.status, "false");
            return new ResponseEntity<>(hm, HttpStatus.BAD_REQUEST);
        }
    }else{
           hm.put(REnum.message, "Session user is null");
           hm.put(REnum.status, "false");
           return new ResponseEntity<>(hm, HttpStatus.BAD_REQUEST);
       }
    }


    public ResponseEntity delete(Long id) {
        Map<REnum, Object> hm = new LinkedHashMap();
        try {
            userRepository.deleteById(id);
            hm.put(REnum.status, true);
            return new ResponseEntity<>(hm, HttpStatus.OK);
        } catch (Exception exception) {
            hm.put(REnum.status, false);
            System.out.println(exception);
            return new ResponseEntity<>(hm, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity settings( String firstName, String lastName, String email, String phone) {
        Map<REnum, Object> hm = new LinkedHashMap();
        try {
            User oldUser = (User) session.getAttribute("user");
            Optional<User> customer1 = userRepository.findByEmailEqualsIgnoreCase(email);

                if ((oldUser.getEmail().equals(email)) || !customer1.isPresent()) {
                    oldUser.setFirstName(firstName);
                    oldUser.setLastName(lastName);
                    oldUser.setEmail(email);
                    oldUser.setPhone(phone);
                    userRepository.saveAndFlush(oldUser);
                    hm.put(REnum.status, true);
                    hm.put(REnum.result, oldUser);
                    return new ResponseEntity<>(hm, HttpStatus.OK);
                } else {
                    hm.put(REnum.status, false);
                    hm.put(REnum.message, "This email already registered");
                    return new ResponseEntity<>(hm, HttpStatus.BAD_REQUEST);
                }
        } catch (Exception exception) {
            hm.put(REnum.status, false);
            hm.put(REnum.message,exception);
            return new ResponseEntity<>(hm, HttpStatus.BAD_REQUEST);
        }

    }


    public ResponseEntity changeEnableCustomer(Long id,boolean enable){
        Map<REnum, Object> hm = new LinkedHashMap();
        Optional<User> optionalCustomer= userRepository.findById(id);
        if(optionalCustomer.isPresent()){
            User user =optionalCustomer.get();
            user.setEnabled(enable);
            userRepository.save(user);
            hm.put(REnum.status,true);
            hm.put(REnum.result, user);
            return new ResponseEntity<>(hm,HttpStatus.OK);
        }
        else {
            hm.put(REnum.status, false);
            hm.put(REnum.message,"Invalid user id");
            return new ResponseEntity<>(hm, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity list() {
        Map<REnum, Object> hm = new HashMap<>();
        List<User> userList = userRepository.findAll();
        hm.put(REnum.status, true);
        hm.put(REnum.result, userList);
        return new ResponseEntity<>(hm, HttpStatus.OK);

    }




}
