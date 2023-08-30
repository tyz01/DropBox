package com.example.dropbox.controller;

import com.example.dropbox.model.User;
import com.example.dropbox.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistration(@RequestParam String username,
                                      @RequestParam String email,
                                      @RequestParam String password) {
        User newUser = new User();
        System.out.println(username);
        newUser.setUsername(username);
        System.out.println(email);
        newUser.setEmail(email);
        newUser.setPassword(password);
        System.out.println(newUser);

        userService.registerUser(newUser);

        // Аутентификация пользователя после регистрации
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(newUser.getUsername(), newUser.getPassword());
        Authentication authentication = authenticationManager.authenticate(authRequest);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/home"; // Перенаправление на страницу после успешной регистрации
    }

}

