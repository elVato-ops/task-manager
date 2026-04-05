package taskmanager.auth;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import taskmanager.auth.dto.LoginRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static taskmanager.TestConstants.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest
{
    @MockBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class Login
    {
        @Test
        public void returns200_whenSuccess() throws Exception
        {
            //GIVEN
            String json = """
                         {
                           "username": "%s",
                           "password": "%s"
                         }"""
                    .formatted(USER_NAME, PASSWORD);

            when(authService.login(any(LoginRequest.class)))
                    .thenReturn(loginResponse());

            //WHEN
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value(TOKEN));

            verify(authService, times(1)).login(any(LoginRequest.class));
            verifyNoMoreInteractions(authService);
        }

        @Test
        public void returns400_whenUsernameEmpty() throws Exception
        {
            //GIVEN
            String json = """
                         {
                           "username": "",
                           "password": "%s"
                         }"""
                    .formatted(PASSWORD);

            //WHEN
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))

            //THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_INVALID"))
                    .andExpect(jsonPath("$.timestamp").exists());

            verifyNoInteractions(authService);
        }

        @Test
        public void returns400_whenPasswordEmpty() throws Exception
        {
            //GIVEN
            String json = """
                         {
                           "username": "%s",
                           "password": ""
                         }"""
                    .formatted(USER_NAME);

            //WHEN
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))

            //THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_INVALID"))
                    .andExpect(jsonPath("$.timestamp").exists());

            verifyNoInteractions(authService);
        }

        @Test
        public void returns401_whenAuthorizationFails() throws Exception
        {
            //GIVEN
            String json = """
                         {
                           "username": "%s",
                           "password": "%s"
                         }"""
                    .formatted(USER_NAME, PASSWORD);

            when(authService.login(any(LoginRequest.class)))
                    .thenThrow(BadCredentialsException.class);

            //WHEN
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))

            //THEN
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"))
                    .andExpect(jsonPath("$.timestamp").exists());

            verify(authService, times(1)).login(any(LoginRequest.class));
            verifyNoMoreInteractions(authService);
        }
    }
}
