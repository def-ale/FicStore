package com.br.store.backend.controller;

import com.br.store.backend.config.UserDetailsConfig;
import com.br.store.backend.dto.UserResponse;
import com.br.store.backend.model.User;
import com.br.store.backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        UserDetailsConfig userDetails = (UserDetailsConfig) authentication.getPrincipal();
        User user = userDetails.getUser();

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole());

        return ResponseEntity.ok(response);
    }

}
