package taskmanager.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import taskmanager.auth.dto.LoginResponse;
import taskmanager.project.dto.ProjectResponse;
import taskmanager.task.TaskStatus;
import taskmanager.task.dto.TaskResponse;
import taskmanager.user.UserRole;
import taskmanager.user.dto.UserResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static taskmanager.TestConstants.PASSWORD;
import static taskmanager.TestConstants.USER_NAME;
import static taskmanager.user.UserRole.USER;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public abstract class BaseIntegrationTest
{
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String token;

    protected Long userId;

    @BeforeEach
    void setUp() throws Exception
    {
        userId = createUser(USER_NAME, PASSWORD, USER);
        loginAndSetToken(USER_NAME, PASSWORD);
    }

    protected void loginAndSetToken(String userName, String password) throws Exception
    {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"username": "%s", "password": "%s"}
                    """.formatted(userName, password)))
                .andExpect(status().isOk())
                .andReturn();

        token = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                LoginResponse.class).token();
    }

    protected ResultActions getTasks() throws Exception
    {
        return mockMvc.perform(withAuth(get("/tasks")));
    }

    protected ResultActions getTasksWithFilter(String taskName) throws Exception
    {
        return mockMvc.perform(withAuth(get("/tasks"))
                .param("name", taskName));
    }

    protected ResultActions getTasks(Long projectId) throws Exception
    {
        return mockMvc.perform(withAuth(get("/projects/" + projectId + "/tasks")));
    }

    protected ResultActions patchUpdateTask(Long id, TaskStatus taskStatus) throws Exception
    {
        String json = """
        {
          "id": "%s",
          "status": "%s"
        }"""
                .formatted(id, taskStatus);

        return mockMvc.perform(withAuth(patch("/tasks/" + id + "/status/" + taskStatus))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    protected Long createTask(String name, Long projectId, Long assigneeId) throws Exception
    {
        MvcResult mvcResult = postCreateTask(name, projectId, assigneeId)
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TaskResponse.class).id();
    }

    protected ResultActions postCreateTask(String name, Long projectId, Long assigneeId) throws Exception
    {
        String json = """
        {
          "name": "%s",
          "assigneeId": "%s"
        }"""
                .formatted(name, assigneeId);

        return mockMvc.perform(withAuth(post("/projects/" + projectId + "/tasks"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    protected ResultActions getProject(Long id) throws Exception
    {
        return mockMvc.perform(withAuth(get("/projects/" + id)));
    }

    protected Long createProject(String name) throws Exception
    {
        MvcResult mvcResult = postCreateProject(name)
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProjectResponse.class).id();
    }

    protected ResultActions postCreateProject(String name) throws Exception
    {
        String json = """
        {
          "name": "%s"
        }"""
                .formatted(name);

        return mockMvc.perform(withAuth(post("/projects"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    protected ResultActions getUser(Long id) throws Exception
    {
        return mockMvc.perform(withAuth(get("/users/" + id)));
    }

    protected ResultActions getUsers() throws Exception
    {
        return mockMvc.perform(withAuth(get("/users")));
    }

    protected Long createUser(String userName, String password, UserRole role) throws Exception
    {
        MvcResult mvcResult = postCreateUser(userName, password, role)
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class).id();
    }

    protected ResultActions postCreateUser(String userName, String password, UserRole role) throws Exception
    {
        String json = """
        {
          "name": "%s",
          "password": "%s",
          "role": "%s"
        }"""
                .formatted(userName, password, role);

        return mockMvc.perform(withAuth(post("/users"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    protected MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder request)
    {
        return request.header("Authorization", "Bearer " + token);
    }

    protected static int toInt(Long value)
    {
        return Integer.parseInt(value.toString());
    }
}