package taskmanager.security;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static taskmanager.TestConstants.*;

public class JwtUtilsTest
{
    private static JwtUtils jwtUtils;

    @BeforeAll
    @SneakyThrows
    public static void setup()
    {
        jwtUtils = new JwtUtils();

        Field secret = JwtUtils.class.getDeclaredField("secret");
        secret.setAccessible(true);
        secret.set(jwtUtils, SECRET);

        Field expiration = JwtUtils.class.getDeclaredField("expiration");
        expiration.setAccessible(true);
        expiration.set(jwtUtils, EXPIRATION);
    }

    @Nested
    class Token
    {
        @Test
        public void returnsToken_whenSuccess()
        {
            //GIVEN
            String token = jwtUtils.generateToken(USER_ID, USER_NAME);

            //WHEN
            Long userId = jwtUtils.extractUserId(token);
            String userName = jwtUtils.extractUsername(token);
            boolean isValid = jwtUtils.isTokenValid(token);

            //THEN
            assertTrue(isValid);
            assertEquals(USER_ID, userId);
            assertEquals(USER_NAME, userName);
        }

        @Test
        public void returnsFalse_whenTokenInvalid()
        {
            //WHEN
            boolean isValid = jwtUtils.isTokenValid(TOKEN);

            //THEN
            assertFalse(isValid);
        }
    }
}
