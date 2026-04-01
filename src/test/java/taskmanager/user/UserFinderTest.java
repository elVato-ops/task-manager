package taskmanager.user;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import taskmanager.exception.NotFoundException;
import taskmanager.exception.ResourceType;
import taskmanager.user.filter.UserFilter;
import taskmanager.user.specification.UserSpecification;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static taskmanager.TestConstants.*;

@ExtendWith(MockitoExtension.class)
public class UserFinderTest
{
    @InjectMocks
    UserFinder userFinder;

    @Mock
    UserRepository userRepository;

    @Nested
    class GetUsers
    {
        @Test
        public void returnsUsers_whenSuccess()
        {
            //GIVEN
            UserFilter filter = UserFilter.builder().build();
            Specification<User> specification = UserSpecification.withFilter(filter);

            when(userRepository.findAll(specification, PAGEABLE)).thenReturn(usersPage());

            //WHEN
            Page<User> users = userFinder.getUsers(specification, PAGEABLE);

            //THEN
            verify(userRepository, times(1)).findAll(specification, PAGEABLE);
            verifyNoMoreInteractions(userRepository);

            User user = users.get().toList().get(0);
            assertEquals(user().getId(), user.getId());
            assertEquals(user().getPassword(), user.getPassword());
        }
    }

    @Nested
    class GetUser
    {
        @Test
        public void returnsUsers_whenSuccess()
        {
            //GIVEN
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user()));

            //WHEN
            User user = userFinder.getUser(USER_ID);

            //THEN
            verify(userRepository, times(1)).findById(USER_ID);
            verifyNoMoreInteractions(userRepository);

            assertEquals(user().getId(), user.getId());
            assertEquals(user().getPassword(), user.getPassword());
        }

        @Test
        public void throwsNotFoundException_whenNotExists()
        {
            //GIVEN
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            //WHEN
            NotFoundException notFoundException =
                    assertThrows(NotFoundException.class, () -> userFinder.getUser(USER_ID));

            //THEN
            verify(userRepository, times(1)).findById(USER_ID);
            verifyNoMoreInteractions(userRepository);

            assertEquals(ResourceType.USER, notFoundException.getResource());
            assertEquals(USER_ID, notFoundException.getId());
        }
    }
}
