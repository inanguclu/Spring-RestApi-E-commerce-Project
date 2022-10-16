package com.works.restcontrollers;

import com.works.entities.User;
import com.works.services.UserService;

import org.hibernate.validator.constraints.Length;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@RestController
@RequestMapping("/user")

@Validated

public class UserRestController {
    final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody User user){
        return userService.register(user);
    }

    @PutMapping("/changePassword")
    public ResponseEntity changePassword(@RequestParam @NotBlank(message = "oldPassword can not be blank") String oldPassword, @RequestParam @NotBlank(message = "password can not be blank") @Pattern(message = "Password must contain min one upper,lower letter and 0-9 digit number ",
            regexp = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,})") String newPassword){
      return  userService.changePassword(oldPassword,newPassword);
    }

    @DeleteMapping("/delete")
    public ResponseEntity delete(@RequestParam Long id){
        return userService.delete(id);
    }

    @PutMapping("/settings")
    public ResponseEntity settings( @RequestParam  @Length(message = "firstName  must contain min 2 max  50 character.", min = 2, max = 50) String firstName, @RequestParam @Length(message = "firstName  must contain min 2 max  50 character.", min = 2, max = 50)  String secondName, @RequestParam @Email(message = "E-mail Format error") String email, @RequestParam  @Length(message = "telephone must contain min 10 max  5O character.", min = 10, max = 50) String telephone ){
        return userService.settings(firstName, secondName,email,telephone);
    }

    @PutMapping("/changeCustomerEnable")
    public ResponseEntity changeEnable(@RequestParam Long id,@RequestParam boolean enable){
        return userService.changeEnableCustomer(id,enable);
    }

    @GetMapping("/list")
    public ResponseEntity list(){
        return userService.list();
    }

}
