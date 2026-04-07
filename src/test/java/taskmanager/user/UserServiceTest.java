package taskmanager.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import taskmanager.auth.AuthService;
import taskmanager.exception.ForbiddenAccessException;
import taskmanager.exception.NameInUseException;
import taskmanager.user.dto.UserResponse;
import taskmanager.user.filter.UserFilter;
import taskmanager.utils.UserMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static taskmanager.TestConstants.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest
{
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFinder userFinder;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthService authService;

    private UserService userService;

    @BeforeEach
    void setUp()
    {
        UserMapper userMapper = new UserMapper(passwordEncoder);
        userService = new UserService(userRepository, userFinder, authService, userMapper);
    }

    @Nested
    class CreateUser
    {
        @Test
        public void returnsUser_whenSuccess()
        {
            //GIVEN
            when(userFinder.existsByName(USER_NAME)).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(user());
            when(passwordEncoder.encode(any())).thenReturn("encoded-password");
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

            //WHEN
            UserResponse userResponse = userService.createUser(createUserRequest());

            //THEN
            verify(userRepository, times(1)).save(captor.capture());
            verifyNoMoreInteractions(userRepository);

            User user = captor.getValue();
            assertEquals(createUserRequest().name(), user.getName());
            assertEquals("encoded-password", user.getPassword());

            assertEquals(createUserRequest().name(), userResponse.name());
        }

        @Test
        public void throws409_whenNameInUse()
        {
            //GIVEN
            when(userFinder.existsByName(USER_NAME))
                    .thenReturn(true);

            //WHEN
            assertThrows(NameInUseException.class,
                    () -> userService.createUser(createUserRequest()));

            //THEN
            verify(userFinder, times(1)).existsByName(USER_NAME);
            verifyNoMoreInteractions(userFinder);
            verifyNoInteractions(userRepository);
        }
    }

    @Nested
    class GetUsers
    {
        @Test
        public void returnsUsers_whenSuccess()
        {
            //GIVEN
            UserFilter filter = UserFilter.builder().build();

            when(userFinder.getUsers(ArgumentMatchers.any(), eq(PAGEABLE)))
                    .thenReturn(usersPage());

            //WHEN
            Page<UserResponse> users = userService.getUsers(filter, USER_ID, PAGEABLE);

            //THEN
            verify(authService, times(1))
                    .verifyAdminRole(USER_ID);

            verifyNoMoreInteractions(authService);

            verify(userFinder, times(1))
                    .getUsers(ArgumentMatchers.any(), eq(PAGEABLE));

            verifyNoMoreInteractions(userFinder);

            assertEquals(1, users.getTotalElements());

            UserResponse userResponse = users.get().toList().get(0);
            assertEquals(user().getId(), userResponse.id());
            assertEquals(user().getName(), userResponse.name());
        }

        @Test
        public void throwsForbiddenAccessException_whenUserHasNoRights()
        {
            //GIVEN
            UserFilter filter = UserFilter.builder().build();

            doThrow(new ForbiddenAccessException(USER_ID, UserRole.ADMIN))
                    .when(authService).verifyAdminRole(USER_ID);

            //WHEN
            assertThrows(ForbiddenAccessException.class,
                    () -> userService.getUsers(filter, USER_ID, PAGEABLE));

            //THEN
            verify(authService, times(1))
                    .verifyAdminRole(USER_ID);

            verifyNoMoreInteractions(authService);
            verifyNoInteractions(userFinder);
        }
    }

    @Nested
    class GetUser
    {
        @Test
        public void returnsUser_whenSuccess()
        {
            //GIVEN
            when(userFinder.getUser(USER_ID))
                    .thenReturn(user());

            //WHEN
            UserResponse userResponse = userService.getUser(USER_ID, OTHER_USER_ID);

            //THEN
            verify(userFinder, times(1))
                    .getUser(USER_ID);

            verifyNoMoreInteractions(userFinder);

            verify(authService, times(1))
                    .verifyAdminRole(OTHER_USER_ID);

            verifyNoMoreInteractions(authService);

            assertEquals(user().getId(), userResponse.id());
            assertEquals(user().getName(), userResponse.name());
        }

        @Test
        public void throwsForbiddenAccessException_whenUserHasNoRights()
        {
            //GIVEN
            doThrow(new ForbiddenAccessException(USER_ID, UserRole.ADMIN))
                    .when(authService).verifyAdminRole(OTHER_USER_ID);

            //WHEN
            assertThrows(ForbiddenAccessException.class,
                    () -> userService.getUser(USER_ID, OTHER_USER_ID));

            //THEN
            verify(authService, times(1))
                    .verifyAdminRole(OTHER_USER_ID);

            verifyNoMoreInteractions(authService);
            verifyNoInteractions(userFinder);
        }
    }
}
