package taskmanager.auth;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import taskmanager.security.JwtUtils;
import taskmanager.user.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static taskmanager.TestConstants.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest
{
    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Nested
    class Login
    {
        @Test
        public void returnsLogin_whenSuccess()
        {
            //GIVEN
            when(userRepository.findByName(USER_NAME)).thenReturn(Optional.of(user()));
            when(passwordEncoder.matches(PASSWORD, PASSWORD)).thenReturn(true);
            when(jwtUtils.generateToken(USER_ID, USER_NAME)).thenReturn(TOKEN);

            //WHEN
            LoginResponse login = authService.login(loginRequest());

            //THEN
            verify(userRepository, times(1)).findByName(USER_NAME);
            verifyNoMoreInteractions(userRepository);

            verify(passwordEncoder, times(1)).matches(PASSWORD, PASSWORD);
            verifyNoMoreInteractions(passwordEncoder);

            verify(jwtUtils, times(1)).generateToken(USER_ID, USER_NAME);
            verifyNoMoreInteractions(jwtUtils);

            assertEquals(TOKEN, login.token());
        }

        @Test
        public void throwsBadCredentialsException_whenUserNotExists()
        {
            //GIVEN
            when(userRepository.findByName(USER_NAME)).thenReturn(Optional.empty());

            //WHEN
            assertThrows(BadCredentialsException.class,
                    () -> authService.login(loginRequest()));

            //THEN
            verify(userRepository, times(1)).findByName(USER_NAME);
            verifyNoMoreInteractions(userRepository);

            verifyNoInteractions(passwordEncoder);
            verifyNoInteractions(jwtUtils);
        }

        @Test
        public void throwsBadCredentialsException_whenPasswordInvalid()
        {
            //GIVEN
            when(userRepository.findByName(USER_NAME)).thenReturn(Optional.of(user()));
            when(passwordEncoder.matches(PASSWORD, PASSWORD)).thenReturn(false);

            //WHEN
            assertThrows(BadCredentialsException.class,
                    () -> authService.login(loginRequest()));

            //THEN
            verify(userRepository, times(1)).findByName(USER_NAME);
            verifyNoMoreInteractions(userRepository);

            verify(passwordEncoder, times(1)).matches(PASSWORD, PASSWORD);
            verifyNoMoreInteractions(passwordEncoder);

            verifyNoInteractions(jwtUtils);
        }
    }
}