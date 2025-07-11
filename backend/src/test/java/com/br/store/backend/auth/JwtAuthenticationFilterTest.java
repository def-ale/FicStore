package com.br.store.backend.auth;

import com.br.store.backend.config.UserDetailsConfig;
import com.br.store.backend.model.User;
import com.br.store.backend.model.enums.Role;
import com.br.store.backend.repository.UserRepository;
import com.br.store.backend.security.JwtAuthenticationFilter;
import com.br.store.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class JwtAuthenticationFilterTest {

    @Autowired private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired private UserRepository userRepository;
    @Autowired private JwtService jwtService;
    @Autowired private PasswordEncoder passwordEncoder;

    private User user;
    private String jwtToken;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        user = new User(null, "Filter User", "filteruser@example.com", passwordEncoder.encode("123456"), Role.CLIENT);
        userRepository.save(user);

        jwtToken = jwtService.generateToken(user);
        SecurityContextHolder.clearContext();
    }

    @Test
    public void shouldAuthenticateUser_whenValidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + jwtToken);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isInstanceOf(UserDetailsConfig.class);

        UserDetailsConfig userDetails = (UserDetailsConfig) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User authenticatedUser = userDetails.getUser();

        assertThat(authenticatedUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void shouldNotAuthenticate_whenNoAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

}
