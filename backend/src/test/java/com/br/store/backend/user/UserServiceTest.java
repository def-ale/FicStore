package com.br.store.backend.user;

import com.br.store.backend.model.User;
import com.br.store.backend.model.enums.Role;
import com.br.store.backend.repository.UserRepository;
import com.br.store.backend.service.UserService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldCreateUser() {
        User user = new User();
        user.setName("Service Test User");
        user.setEmail("serviceuser@example.com");
        user.setPassword("123456");
        user.setRole(Role.CLIENT);

        User created = userService.createUser(user);
        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals("Service Test User", created.getName());

        userRepository.deleteAll();
    }

    @Test
    public void shouldFindUserByEmail() {
        User user = new User(null, "Service Find User", "finduser@example.com",
                "123456", Role.CLIENT);
        userRepository.save(user);

        User found = userService.findByEmail("finduser@example.com");
        Assertions.assertNotNull(found);
        Assertions.assertEquals("finduser@example.com", found.getEmail());

        userRepository.deleteAll();
    }
}
