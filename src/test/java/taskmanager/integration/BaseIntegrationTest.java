package taskmanager.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import taskmanager.project.dto.ProjectResponse;
import taskmanager.task.dto.TaskResponse;
import taskmanager.user.dto.UserResponse;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@WithMockUser
public abstract class BaseIntegrationTest
{
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    protected ResultActions getTasks() throws Exception
    {
        return mockMvc.perform(get("/tasks"));
    }

    protected ResultActions getTasksWithFilter(String taskName) throws Exception
    {
        return mockMvc.perform(get("/tasks")
                .param("name", taskName));
    }

    protected ResultActions getTasks(Long projectId) throws Exception
    {
        return mockMvc.perform(get("/projects/" + projectId + "/tasks"));
    }

    protected Long createTask(String name, Long userId, Long projectId) throws Exception
    {
        MvcResult mvcResult = postCreateTask(name, userId, projectId)
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TaskResponse.class).id();
    }

    protected ResultActions postCreateTask(String name, Long userId, Long projectId) throws Exception
    {
        String json = """
        {
          "name": "%s",
          "userId": "%s"
        }"""
                .formatted(name, userId);

        return mockMvc.perform(post("/projects/" + projectId + "/tasks")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    protected ResultActions getProject(Long id) throws Exception
    {
        return mockMvc.perform(get("/projects/" + id));
    }

    protected Long createProject(String name, Long userId) throws Exception
    {
        MvcResult mvcResult = postCreateProject(name, userId)
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProjectResponse.class).id();
    }

    protected ResultActions postCreateProject(String name, Long userId) throws Exception
    {
        String json = """
        {
          "name": "%s",
          "userId": "%s"
        }"""
                .formatted(name, userId);

        return mockMvc.perform(post("/projects")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    protected ResultActions getUser(Long id) throws Exception
    {
        return mockMvc.perform(get("/users/" + id));
    }

    protected ResultActions getUsers() throws Exception
    {
        return mockMvc.perform(get("/users"));
    }

    protected Long createUser(String userName, String password) throws Exception
    {
        MvcResult mvcResult = postCreateUser(userName, password)
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class).id();
    }

    protected ResultActions postCreateUser(String userName, String password) throws Exception
    {
        String json = """
        {
          "name": "%s",
          "password": "%s"
        }"""
                .formatted(userName, password);

        return mockMvc.perform(post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    protected static int toInt(Long value)
    {
        return Integer.parseInt(value.toString());
    }
}