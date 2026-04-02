package taskmanager.project;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import taskmanager.exception.NotFoundException;
import taskmanager.exception.ResourceType;
import taskmanager.project.dto.CreateProjectRequest;
import taskmanager.project.filter.ProjectFilter;
import taskmanager.task.TaskService;
import taskmanager.task.dto.CreateTaskRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static taskmanager.TestConstants.*;

@WebMvcTest(ProjectController.class)
@WithMockUser
public class ProjectControllerTest
{
    @MockBean
    private ProjectService projectService;

    @MockBean
    private TaskService taskService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    public class GetProjectById
    {
        @Test
        public void returns200_whenExists() throws Exception
        {
            //GIVEN
            when(projectService.getProject(PROJECT_ID)).thenReturn(projectResponse());

            //WHEN
            mockMvc.perform(get("/projects/" + PROJECT_ID))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(PROJECT_ID))
                    .andExpect(jsonPath("$.name").value(PROJECT_NAME))
                    .andExpect(jsonPath("$.ownerId").value(USER_ID));

            verify(projectService, times(1)).getProject(PROJECT_ID);
            verifyNoMoreInteractions(projectService);
        }

        @Test
        public void returns400_whenIdInvalid() throws Exception
        {
            //WHEN
            mockMvc.perform(get("/projects/" + -PROJECT_ID))

            //THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_INVALID"))
                    .andExpect(jsonPath("$.timestamp").exists());

            verifyNoInteractions(projectService);
        }

        @Test
        public void returns404_whenNotExists() throws Exception
        {
            //GIVEN
            when(projectService.getProject(PROJECT_ID))
                    .thenThrow(new NotFoundException(PROJECT_ID, ResourceType.PROJECT));

            //WHEN
            mockMvc.perform(get("/projects/" + PROJECT_ID))

                    //THEN
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.resource").value("PROJECT"))
                    .andExpect(jsonPath("$.timestamp").exists());

            verify(projectService, times(1)).getProject(PROJECT_ID);
            verifyNoMoreInteractions(projectService);
        }
    }

    @Nested
    public class GetAllProjects
    {
        @Test
        public void returns200_whenRequestValid() throws Exception
        {
            //GIVEN
            when(projectService.getProjects(any(ProjectFilter.class), eq(PAGEABLE)))
                    .thenReturn(projectResponsePage());
        
            //WHEN
            mockMvc.perform(get("/projects")
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize()))
                        .param("name", PROJECT_NAME)
                        .param("ownerId", USER_ID.toString()))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size").value(1))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].name").value(PROJECT_NAME))
                    .andExpect(jsonPath("$.content[0].id").value(PROJECT_ID))
                    .andExpect(jsonPath("$.content[0].ownerId").value(USER_ID));

            verify(projectService, times(1))
                    .getProjects(any(ProjectFilter.class), eq(PAGEABLE));

            verifyNoMoreInteractions(projectService);
        }

        @Test
        public void returns200_whenNoFilters() throws Exception
        {
            //GIVEN
            when(projectService.getProjects(any(ProjectFilter.class), any(Pageable.class)))
                    .thenReturn(projectResponsePage());

            //WHEN
            mockMvc.perform(get("/projects"))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].name").value(PROJECT_NAME))
                    .andExpect(jsonPath("$.content[0].id").value(PROJECT_ID))
                    .andExpect(jsonPath("$.content[0].ownerId").value(USER_ID));

            verify(projectService, times(1))
                    .getProjects(any(ProjectFilter.class), any(Pageable.class));

            verifyNoMoreInteractions(projectService);
        }

        @Test
        public void returns400_whenNegativeOwnerId() throws Exception
        {
            //WHEN
            mockMvc.perform(get("/projects")
                        .param("ownerId", "-20"))

            //THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_INVALID"))
                    .andExpect(jsonPath("$.timestamp").exists());

            verifyNoInteractions(projectService);
        }
    }

    @Nested
    class CreateProject
    {
        @Test
        public void returns201_whenRequestValid() throws Exception
        {
            //GIVEN
            String json = """
            {
              "name": "Some project",
              "userId": "17"
            }""";

            when(projectService.createProject(any(CreateProjectRequest.class)))
                    .thenReturn(projectResponse());

            ArgumentCaptor<CreateProjectRequest> captor =
                    ArgumentCaptor.forClass(CreateProjectRequest.class);

            //WHEN
            mockMvc.perform(post("/projects")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))

            //THEN
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/projects/" + PROJECT_ID))
                    .andExpect(jsonPath("$.id").value(PROJECT_ID))
                    .andExpect(jsonPath("$.name").value(PROJECT_NAME))
                    .andExpect(jsonPath("$.ownerId").value(USER_ID));

            verify(projectService, times(1)).createProject(captor.capture());
            verifyNoMoreInteractions(projectService);

            CreateProjectRequest value = captor.getValue();
            assertEquals(USER_ID, value.userId());
            assertEquals(PROJECT_NAME, value.name());
        }

        @Test
        public void returns400_whenNameEmpty() throws Exception
        {
            //GIVEN
            String json = """
            {
              "name": "",
              "userId": "12"
            }""";

            //WHEN
            mockMvc.perform(post("/projects")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))

            //THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_INVALID"))
                    .andExpect(jsonPath("$.timestamp").exists());

            verifyNoInteractions(projectService);
        }

        @Test
        public void returns400_whenOwnerIdInvalid() throws Exception
        {
            //GIVEN
            String json = """
            {
              "name": "Some project",
              "userId": "-12"
            }""";

            //WHEN
            mockMvc.perform(post("/projects")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))

                    //THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_INVALID"))
                    .andExpect(jsonPath("$.timestamp").exists());

            verifyNoInteractions(projectService);
        }

    }

    @Nested
    class CreateTask
    {
        @Test
        public void returns201_whenRequestValid() throws Exception
        {
            //GIVEN
            String json = """
            {
              "name": "Some task",
              "userId": "17"
            }""";

            when(taskService.createTask(any(CreateTaskRequest.class), eq(PROJECT_ID)))
                    .thenReturn(taskResponse());

            ArgumentCaptor<CreateTaskRequest> captor =
                    ArgumentCaptor.forClass(CreateTaskRequest.class);

            //WHEN
            mockMvc.perform(post("/projects/" + PROJECT_ID + "/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

            //THEN
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/projects/" + PROJECT_ID + "/tasks/" + TASK_ID))
                    .andExpect(jsonPath("$.id").value(TASK_ID))
                    .andExpect(jsonPath("$.name").value(TASK_NAME))
                    .andExpect(jsonPath("$.status").value(TASK_STATUS.toString()))
                    .andExpect(jsonPath("$.projectId").value(PROJECT_ID))
                    .andExpect(jsonPath("$.assigneeId").value(USER_ID));

            verify(taskService, times(1)).createTask(captor.capture(), eq(PROJECT_ID));
            verifyNoMoreInteractions(taskService);

            CreateTaskRequest task = captor.getValue();
            assertEquals(TASK_NAME, task.name());
            assertEquals(USER_ID, task.userId());
        }
        
        @Test
        public void returns400_whenProjectIdInvalid() throws Exception
        {
            //GIVEN
            String json = """
            {
              "name": "Some task",
              "userId": "17"
            }""";

            //WHEN
            mockMvc.perform(post("/projects/" + -PROJECT_ID + "/tasks")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))

            //THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_INVALID"))
                    .andExpect(jsonPath("$.timestamp").exists());

            verifyNoInteractions(taskService);
        }
        
        @Test
        public void returns404_whenProjectNotExists() throws Exception
        {
            //GIVEN
            String json = """
            {
              "name": "Some task",
              "userId": "17"
            }""";

            when(taskService.createTask(any(CreateTaskRequest.class), eq(PROJECT_ID)))
                    .thenThrow(new NotFoundException(PROJECT_ID, ResourceType.PROJECT));

            //WHEN
            mockMvc.perform(post("/projects/" + PROJECT_ID + "/tasks")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))

                    //THEN
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.resource").value(ResourceType.PROJECT.toString()))
                    .andExpect(jsonPath("$.message").value("PROJECT with id " + PROJECT_ID + " not found"));
        }

        @Test
        public void returns404_whenUserNotExists() throws Exception
        {
            //GIVEN
            String json = """
            {
              "name": "Some task",
              "userId": "17"
            }""";

            when(taskService.createTask(any(CreateTaskRequest.class), eq(PROJECT_ID)))
                    .thenThrow(new NotFoundException(USER_ID, ResourceType.USER));

            //WHEN
            mockMvc.perform(post("/projects/" + PROJECT_ID + "/tasks")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))

                    //THEN
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.resource").value(ResourceType.USER.toString()))
                    .andExpect(jsonPath("$.message").value("USER with id " + USER_ID + " not found"));
        }
    }

    @Nested
    class GetTasks
    {
        @Test
        public void returns200_whenRequestValid() throws Exception
        {
            //GIVEN
            when(taskService.getTasks(eq(PROJECT_ID), any(Pageable.class)))
                    .thenReturn(taskResponsePage());

            //WHEN
            mockMvc.perform(get("/projects/" + PROJECT_ID + "/tasks")
                    .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                    .param("size", String.valueOf(PAGEABLE.getPageSize())))

            //THEN
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.size").value(1))
                        .andExpect(jsonPath("$.totalElements").value(1))
                        .andExpect(jsonPath("$.totalPages").value(1))
                        .andExpect(jsonPath("$.content.length()").value(1))
                        .andExpect(jsonPath("$.content[0].name").value(TASK_NAME))
                        .andExpect(jsonPath("$.content[0].id").value(TASK_ID))
                        .andExpect(jsonPath("$.content[0].assigneeId").value(USER_ID))
                        .andExpect(jsonPath("$.content[0].projectId").value(PROJECT_ID))
                        .andExpect(jsonPath("$.content[0].status").value(TASK_STATUS.toString()));

            verify(taskService, times(1)).getTasks(eq(PROJECT_ID), any(Pageable.class));
            verifyNoMoreInteractions(taskService);
        }

        @Test
        public void returns400_whenProjectIdNegative() throws Exception
        {
            //GIVEN

            //WHEN
            mockMvc.perform(get("/projects/" + -PROJECT_ID + "/tasks"))

            //THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_INVALID"))
                    .andExpect(jsonPath("$.timestamp").exists());

            verifyNoInteractions(taskService);
        }

        @Test
        public void returns404_whenProjectNotExists() throws Exception
        {
            //GIVEN
            when(taskService.getTasks(eq(PROJECT_ID), any(Pageable.class)))
                    .thenThrow(new NotFoundException(PROJECT_ID, ResourceType.PROJECT));

            //WHEN
            mockMvc.perform(get("/projects/" + PROJECT_ID + "/tasks"))

            //THEN
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.resource").value(ResourceType.PROJECT.toString()))
                    .andExpect(jsonPath("$.message").value("PROJECT with id " + PROJECT_ID + " not found"));

            verify(taskService, times(1)).getTasks(eq(PROJECT_ID), any(Pageable.class));
            verifyNoMoreInteractions(taskService);
        }
    }
}