package com.br.store.backend.controller;

import com.br.store.backend.dto.AuthenticationResponse;
import com.br.store.backend.dto.LoginRequest;
import com.br.store.backend.dto.RegisterRequest;
import com.br.store.backend.model.User;
import com.br.store.backend.model.enums.Role;
import com.br.store.backend.repository.UserRepository;
import com.br.store.backend.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // React access point
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CLIENT);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("User created!");
    }



    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();  // ou retornar mensagem JSON se preferir
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // idem
        }

        String token = jwtService.generateToken(user);
        System.out.println("Generated JWT: " + token);
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }
}


