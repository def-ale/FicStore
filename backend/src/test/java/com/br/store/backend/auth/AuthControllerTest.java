package com.br.store.backend.auth;

import com.br.store.backend.dto.RegisterRequest;
import com.br.store.backend.model.User;
import com.br.store.backend.model.enums.Role;
import com.br.store.backend.repository.UserRepository;
import com.br.store.backend.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Test
    public void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("testuser@example.com");
        registerRequest.setPassword("123456");
        registerRequest.setRole(Role.CLIENT);

        String json = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().string("User created!"));

        userRepository.deleteAll();

    }

    @Test
    public void shouldLoginSuccessfully() throws Exception {
        User user = new User(null, "Test User", "loginuser@example.com",
                passwordEncoder.encode("123456"), Role.CLIENT);
        userRepository.save(user);

        String jsonLogin = """
            {
                "email": "loginuser@example.com",
                "password": "123456"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLogin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        userRepository.deleteAll();
    }

    @Test
    public void shouldFailLoginWithIncorrectPassword() throws Exception {
        User user = new User(null, "Test User", "wrongpass@example.com",
                passwordEncoder.encode("123456"), Role.CLIENT);
        userRepository.save(user);

        String jsonLogin = """
            {
                "email": "wrongpass@example.com",
                "password": "wrongpassword"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLogin))
                .andExpect(status().isUnauthorized());

        userRepository.deleteAll();
    }
}
