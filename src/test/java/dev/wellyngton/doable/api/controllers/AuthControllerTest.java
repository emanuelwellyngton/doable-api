package dev.wellyngton.doable.api.controllers;

import dev.wellyngton.doable.api.dto.AuthResponse;
import dev.wellyngton.doable.api.dto.LoginRequest;
import dev.wellyngton.doable.api.dto.RegisterRequest;
import dev.wellyngton.doable.security.JwtService;
import dev.wellyngton.doable.security.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void register_shouldReturnCreatedWithToken() throws Exception {
        AuthResponse response = new AuthResponse("test-token", "testuser");
        when(userService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"password\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", is("test-token")))
                .andExpect(jsonPath("$.username", is("testuser")));
    }

    @Test
    void register_shouldReturnConflictWhenUsernameExists() throws Exception {
        when(userService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Username already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"existing\",\"password\":\"password\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void login_shouldReturnOkWithToken() throws Exception {
        AuthResponse response = new AuthResponse("test-token", "testuser");
        when(userService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("test-token")))
                .andExpect(jsonPath("$.username", is("testuser")));
    }

    @Test
    void login_shouldReturnUnauthorizedForInvalidCredentials() throws Exception {
        when(userService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());
    }
}
