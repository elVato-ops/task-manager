package taskmanager.integration;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import taskmanager.project.dto.ProjectResponse;
import taskmanager.task.dto.TaskResponse;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static taskmanager.TestConstants.*;

public class TaskManagerIntegrationTest extends BaseIntegrationTest
{
    @Nested
    class UserCreation
    {
        @Test
        public void returnsUser_whenSuccess() throws Exception
        {
            //WHEN
            getUser(userId)

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(USER_NAME))
                    .andExpect(jsonPath("$.id").value(userId));
        }

        @Test
        public void returnsUsers_whenGetAll() throws Exception
        {
            //GIVEN
            Long otherUserId = createUser(OTHER_USER_NAME, PASSWORD);

            //WHEN
            getUsers()

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.content[*].name",
                            containsInAnyOrder(USER_NAME, OTHER_USER_NAME)))
                    .andExpect(jsonPath("$.content[*].id",
                            containsInAnyOrder(toInt(userId), toInt(otherUserId))));
        }

        @Test
        public void returns400_whenNameEmpty() throws Exception
        {
            //WHEN
            postCreateUser("", PASSWORD)

            //THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_INVALID"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        public void returns404_whenNotExists() throws Exception
        {
            //WHEN
            getUser(997L)

            //THEN
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.resource").value("USER"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }

    @Nested
    class ProjectCreation
    {
        @Test
        public void returnsProject_whenSuccess() throws Exception
        {
            //WHEN
            MvcResult result = postCreateProject(PROJECT_NAME)

            //THEN
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value(PROJECT_NAME))
                    .andExpect(jsonPath("$.ownerId").value(userId))
                    .andReturn();

            //AND GIVEN
            Long projectId = objectMapper.readValue(result.getResponse().getContentAsString(), ProjectResponse.class).id();

            //WHEN
            getProject(projectId)

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(PROJECT_NAME))
                    .andExpect(jsonPath("$.id").value(projectId))
                    .andExpect(jsonPath("$.ownerId").value(userId));
        }

        @Test
        public void returns400_whenNameEmpty() throws Exception
        {
            //WHEN
            postCreateProject("")

            //THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_INVALID"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        public void returns404_whenNotExists() throws Exception
        {
            //WHEN
            getProject(997L)

                    //THEN
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.resource").value("PROJECT"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }

    @Nested
    class TaskCreation
    {
        @Test
        public void returnsTask_whenSuccess() throws Exception
        {
            //GIVEN
            Long projectId = createProject(PROJECT_NAME);

            //WHEN
            MvcResult result = postCreateTask(TASK_NAME, projectId)

            //THEN
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value(TASK_NAME))
                    .andExpect(jsonPath("$.status").value(TASK_STATUS.toString()))
                    .andExpect(jsonPath("$.projectId").value(projectId))
                    .andExpect(jsonPath("$.assigneeId").value(userId))
                    .andReturn();

            //AND GIVEN
            Long taskId = objectMapper.readValue(result.getResponse().getContentAsString(), TaskResponse.class).id();

            //WHEN
            getTasks(projectId)

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].name").value(TASK_NAME))
                    .andExpect(jsonPath("$.content[0].id").value(taskId))
                    .andExpect(jsonPath("$.content[0].assigneeId").value(userId))
                    .andExpect(jsonPath("$.content[0].projectId").value(projectId))
                    .andExpect(jsonPath("$.content[0].status").value(TASK_STATUS.toString()));

            //AND WHEN
            getTasks()
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].id").value(taskId))
                    .andExpect(jsonPath("$.content[0].name").value(TASK_NAME))
                    .andExpect(jsonPath("$.content[0].status").value(TASK_STATUS.toString()))
                    .andExpect(jsonPath("$.content[0].projectId").value(projectId))
                    .andExpect(jsonPath("$.content[0].assigneeId").value(userId));
        }

        @Test
        public void returnsFilteredTask_whenSuccess() throws Exception
        {
            //GIVEN
            Long projectId = createProject(PROJECT_NAME);
            createTask(TASK_NAME, projectId);
            createTask(OTHER_TASK_NAME, projectId);

            //WHEN
            getTasksWithFilter(TASK_NAME)

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].name").value(TASK_NAME))
                    .andExpect(jsonPath("$.content[0].status").value(TASK_STATUS.toString()))
                    .andExpect(jsonPath("$.content[0].projectId").value(projectId))
                    .andExpect(jsonPath("$.content[0].assigneeId").value(userId));
        }

        @Test
        public void returns400_whenNameEmpty() throws Exception
        {
            //GIVEN
            Long projectId = createProject(PROJECT_NAME);

            //WHEN
            postCreateTask("", projectId)

            //THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_INVALID"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }
}