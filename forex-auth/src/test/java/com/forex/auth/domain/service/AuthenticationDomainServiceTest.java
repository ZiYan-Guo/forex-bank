package com.forex.auth.domain.service;

import com.forex.auth.domain.model.aggregate.User;
import com.forex.auth.domain.repository.UserRepository;

import cn.hutool.crypto.digest.BCrypt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationDomainServiceTest {

    @Mock private UserRepository userRepository;

    private AuthenticationDomainService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthenticationDomainService(userRepository);
    }

    @Test
    @DisplayName("Authenticate valid user returns user with roles")
    void testAuthenticate_Success() {
        String rawPassword = "TestPass123";
        String encodedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
        User user = User.create("testuser", encodedPassword, "Test User",
                "test@example.com", "13800138000");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.findRolesByUserId(any())).thenReturn(Set.of());

        User result = authService.authenticate("testuser", rawPassword);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertTrue(result.isActive());
    }

    @Test
    @DisplayName("Authenticate wrong password throws")
    void testAuthenticate_WrongPassword() {
        String encodedPassword = BCrypt.hashpw("CorrectPass", BCrypt.gensalt(12));
        User user = User.create("testuser", encodedPassword, "Test User",
                "test@example.com", "13800138000");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> authService.authenticate("testuser", "WrongPass"));
    }

    @Test
    @DisplayName("Authenticate disabled user throws")
    void testAuthenticate_DisabledUser() {
        String rawPassword = "TestPass123";
        String encodedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
        User user = User.create("testuser", encodedPassword, "Test User",
                "test@example.com", "13800138000");
        user.disable();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> authService.authenticate("testuser", rawPassword));
    }

    @Test
    @DisplayName("Authenticate non-existent username throws")
    void testAuthenticate_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> authService.authenticate("unknown", "any"));
    }

    @Test
    @DisplayName("Encode password returns hashed string different from input")
    void testEncodePassword() {
        String encoded = AuthenticationDomainService.encodePassword("MySecret");
        assertNotNull(encoded);
        assertNotEquals("MySecret", encoded);
        assertTrue(BCrypt.checkpw("MySecret", encoded));
    }

    @Test
    @DisplayName("Encode password produces different hashes for same input")
    void testEncodePassword_DifferentSalts() {
        String hash1 = AuthenticationDomainService.encodePassword("password");
        String hash2 = AuthenticationDomainService.encodePassword("password");
        assertNotEquals(hash1, hash2);
    }
}
