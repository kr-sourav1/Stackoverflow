package com.stackoverflow.beta.service;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.Role;
import com.stackoverflow.beta.model.User;
import com.stackoverflow.beta.model.request.UserDetailsInput;
import com.stackoverflow.beta.repository.RoleRepository;
import com.stackoverflow.beta.repository.UserRepository;
import com.stackoverflow.beta.service.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        UserDetailsInput userDetailsInput = getUserInfo();

        User user = User.builder()
                .name("Test User")
                .email("test.user@gmail.com")
                .password("password")
                .id(1)
                .build();

        when(userRepository.findByEmail(anyString()))
                .thenReturn(null);

        when(roleRepository.findByName(anyString()))
                .thenReturn(Optional.of(Role.builder().name("ADMIN").build()));

        when(userRepository.save(any(User.class))).thenReturn(user);

        when(passwordEncoder.encode(anyString())).thenReturn("password");

        User result = userDetailsService.registerUser(userDetailsInput);
        assertNotNull(result);
        assertEquals("Test User", result.getName());
    }

    @Test
    void testRegisterUser_Failure() {
        UserDetailsInput userDetailsInput = getUserInfo();
        when(userRepository.findByEmail("test.user@gmail.com")).thenReturn(
               User.builder()
                        .name("Test User")
                        .email("test.user@gmail.com")
                        .id(1)
                        .build()
        );

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userDetailsService.registerUser(userDetailsInput);
        });

        assertEquals("User with this email already exists.", exception.getMessage());
    }

    @Test
    void testGetUserById() {
        Optional<User> expectedUser = Optional.of(
                User.builder()
                        .name("Test User")
                        .email("test.user@gmail.com")
                        .id(1)
                        .build()
        );
        when(userRepository.findById(1)).thenReturn(expectedUser);

        Optional<User> actualUser = userDetailsService.getUserById(1);
        assertTrue(actualUser.isPresent());
        assertEquals("Test User", actualUser.get().getName());
    }
    private UserDetailsInput getUserInfo() {
        return new UserDetailsInput("Test User", "test.user@gmail.com", "password");
    }

}
