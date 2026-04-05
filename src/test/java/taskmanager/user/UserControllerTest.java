package taskmanager.user;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import taskmanager.BaseControllerTest;
import taskmanager.exception.NotFoundException;
import taskmanager.exception.ResourceType;
import taskmanager.user.dto.CreateUserRequest;
import taskmanager.user.filter.UserFilter;

import java.net.URI;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static taskmanager.TestConstants.*;

@WebMvcTest(UserController.class)
public class UserControllerTest extends BaseControllerTest
{
    @MockBean
    private UserService userService;

    @Nested
    class CreateUser
    {
        @Test
        public void returns201_whenRequestValid() throws Exception
        {
            //GIVEN
            String json = """
            {
              "name": "Bobek",
              "password": "password"
            }""";

            when(userService.createUser(any(CreateUserRequest.class)))
                    .thenReturn(userResponse());

            ArgumentCaptor<CreateUserRequest> captor = ArgumentCaptor.forClass(CreateUserRequest.class);

            //WHEN
            mockMvc.perform(post(URI.create("/users"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))

            //THEN
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/users/" + USER_ID))
                    .andExpect(jsonPath("$.id").value(USER_ID))
                    .andExpect(jsonPath("$.name").value(USER_NAME));

            verify(userService, times(1)).createUser(captor.capture());
            verifyNoMoreInteractions(userService);

            CreateUserRequest value = captor.getValue();
            assertEquals(USER_NAME, value.name());
            assertEquals(PASSWORD, value.password());
        }

        @Test
        public void returns400_whenNameBlank() throws Exception
        {
            //GIVEN
            String json = """
            {
              "name": "",
              "password": "password"
            }""";

            //WHEN
            mockMvc.perform(post(URI.create("/users"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))

            //THEN
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(userService);
        }

        @Test
        public void returns400_whenPasswordBlank() throws Exception
        {
            //GIVEN
            String json = """
            {
              "name": "Bobek",
              "password": ""
            }""";

            //WHEN
            mockMvc.perform(post(URI.create("/users"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))

            //THEN
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(userService);
        }
    }

    @Nested
    class GetUserById
    {
        @Test
        public void returns200_whenUserExists() throws Exception
        {
            //GIVEN
            when(userService.getUser(USER_ID)).thenReturn(userResponse());

            //WHEN
            mockMvc.perform(get("/users/" + USER_ID))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(USER_ID))
                    .andExpect(jsonPath("$.name").value(USER_NAME));

            verify(userService, times(1)).getUser(USER_ID);
            verifyNoMoreInteractions(userService);
        }

        @Test
        public void returns400_whenIdInvalid() throws Exception
        {
            //GIVEN

            //WHEN
            mockMvc.perform(get("/users/" + -USER_ID))

            //THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_INVALID"))
                    .andExpect(jsonPath("$.timestamp").exists());

            verifyNoInteractions(userService);
        }

        @Test
        public void returns404_whenUserNotExists() throws Exception
        {
            //GIVEN
            when(userService.getUser(USER_ID))
                    .thenThrow(new NotFoundException(USER_ID, ResourceType.USER));

            //WHEN
            mockMvc.perform(get("/users/" + USER_ID))

            //THEN
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.resource").value("USER"))
                    .andExpect(jsonPath("$.timestamp").exists());

            verify(userService, times(1)).getUser(USER_ID);
            verifyNoMoreInteractions(userService);
        }
    }

    @Nested
    class GetUsers
    {
        @Test
        public void returns200_whenRequestValid() throws Exception
        {
            //GIVEN
            when(userService.getUsers(any(UserFilter.class), any(Pageable.class)))
                    .thenReturn(usersResponsePage());

            //WHEN
            mockMvc.perform(get("/users")
                            .param("name", "a")
                            .param("fromCreationDate", INSTANT_STRING)
                            .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                            .param("size", String.valueOf(PAGEABLE.getPageSize())))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(PAGEABLE.getPageNumber()))
                    .andExpect(jsonPath("$.size").value(2))
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.totalPages").value(1))

                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.content[*].name",
                            containsInAnyOrder(USER_NAME, OTHER_USER_NAME)))
                    .andExpect(jsonPath("$.content[*].id",
                            containsInAnyOrder(toInt(USER_ID), toInt(OTHER_USER_ID))));

            verify(userService, times(1))
                    .getUsers(any(UserFilter.class), eq(PAGEABLE));

            verifyNoMoreInteractions(userService);
        }

        @Test
        public void returns200_whenNoFilters() throws Exception
        {
            //GIVEN
            when(userService.getUsers(any(UserFilter.class), any(Pageable.class)))
                    .thenReturn(usersResponsePage());

            //WHEN
            mockMvc.perform(get("/users"))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.content[*].name",
                            containsInAnyOrder(USER_NAME, OTHER_USER_NAME)))
                    .andExpect(jsonPath("$.content[*].id",
                            containsInAnyOrder(toInt(USER_ID), toInt(OTHER_USER_ID))));

            verify(userService, times(1))
                    .getUsers(any(UserFilter.class), any(Pageable.class));

            verifyNoMoreInteractions(userService);
        }

        @Test
        public void returns400_whenFutureCreationDate() throws Exception
        {
            //GIVEN

            //WHEN
            mockMvc.perform(get("/users")
                    .param("fromCreationDate", "3024-01-15T10:30:00Z")
                    .param("page", "0")
                    .param("size", "10"))

            //THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_INVALID"))
                    .andExpect(jsonPath("$.timestamp").exists());

            verifyNoInteractions(userService);
        }
    }

    private static int toInt(Long value)
    {
        return Integer.parseInt(value.toString());
    }
}
