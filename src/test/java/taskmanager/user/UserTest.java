package taskmanager.user;

import org.junit.jupiter.api.Test;
import taskmanager.exception.ResourceType;
import taskmanager.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.*;
import static taskmanager.TestConstants.PASSWORD;
import static taskmanager.TestConstants.USER_NAME;

public class UserTest
{
    @Test
    public void constructorTest()
    {
        ValidationException e = assertThrows(ValidationException.class, () -> new User("", "password"));
        assertEquals(ResourceType.USER, e.getResource());
        assertEquals("User name must not be empty", e.getMessage());

        e = assertThrows(ValidationException.class, () -> new User(null, "password"));
        assertEquals(ResourceType.USER, e.getResource());
        assertEquals("User name must not be empty", e.getMessage());

        e = assertThrows(ValidationException.class, () -> new User("name", ""));
        assertEquals(ResourceType.USER, e.getResource());
        assertEquals("Password must not be empty", e.getMessage());

        e = assertThrows(ValidationException.class, () -> new User("name", null));
        assertEquals(ResourceType.USER, e.getResource());
        assertEquals("Password must not be empty", e.getMessage());

        User user = new User(USER_NAME, PASSWORD);
        assertEquals(USER_NAME, user.getName());
        assertEquals(PASSWORD, user.getPassword());
        assertNotNull(user.getCreationDate());
    }
}
