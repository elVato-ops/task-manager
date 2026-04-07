package taskmanager.user;

import org.junit.jupiter.api.Test;
import taskmanager.exception.ResourceType;
import taskmanager.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.*;
import static taskmanager.TestConstants.*;

public class UserTest
{
    @Test
    public void constructorTest()
    {
        ValidationException e = assertThrows(ValidationException.class, () -> new User("", USER_ROLE, PASSWORD));
        assertEquals(ResourceType.USER, e.getResource());
        assertEquals("User name must not be empty", e.getMessage());

        e = assertThrows(ValidationException.class, () -> new User(null, USER_ROLE, PASSWORD));
        assertEquals(ResourceType.USER, e.getResource());
        assertEquals("User name must not be empty", e.getMessage());

        e = assertThrows(ValidationException.class, () -> new User(USER_NAME, USER_ROLE, ""));
        assertEquals(ResourceType.USER, e.getResource());
        assertEquals("Password must not be empty", e.getMessage());

        e = assertThrows(ValidationException.class, () -> new User(USER_NAME, USER_ROLE, null));
        assertEquals(ResourceType.USER, e.getResource());
        assertEquals("Password must not be empty", e.getMessage());

        e = assertThrows(ValidationException.class, () -> new User(USER_NAME, null, PASSWORD));
        assertEquals(ResourceType.USER, e.getResource());
        assertEquals("You must specify role for user", e.getMessage());

        User user = new User(USER_NAME, USER_ROLE, PASSWORD);
        assertEquals(USER_NAME, user.getName());
        assertEquals(PASSWORD, user.getPassword());
        assertNotNull(user.getCreationDate());
    }
}
