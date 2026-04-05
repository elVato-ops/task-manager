package taskmanager.task;


import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import taskmanager.auth.JwtAuthFilter;
import taskmanager.auth.JwtUtils;
import taskmanager.task.filter.TaskFilter;
import taskmanager.utils.WithMockUserId;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static taskmanager.TestConstants.*;

@WebMvcTest(TaskController.class)
@WithMockUserId
public class TaskControllerTest
{
    @MockBean
    private TaskService taskService;

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    protected JwtAuthFilter jwtAuthFilter;

    @Nested
    class GetAllTasks
    {
        @Test
        public void returns200_whenRequestValid() throws Exception
        {
            //GIVEN
            when(taskService.getTasks(any(TaskFilter.class), any(Pageable.class)))
                    .thenReturn(taskResponsePage());

            //WHEN
            mockMvc.perform(get("/tasks")
                            .param("name", TASK_NAME)
                            .param("status", TASK_STATUS.toString())
                            .param("projectId", PROJECT_ID.toString())
                            .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                            .param("size", String.valueOf(PAGEABLE.getPageSize())))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(PAGEABLE.getPageNumber()))
                    .andExpect(jsonPath("$.size").value(1))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1))

                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].id").value(TASK_ID))
                    .andExpect(jsonPath("$.content[0].name").value(TASK_NAME))
                    .andExpect(jsonPath("$.content[0].status").value(TASK_STATUS.toString()))
                    .andExpect(jsonPath("$.content[0].projectId").value(PROJECT_ID))
                    .andExpect(jsonPath("$.content[0].assigneeId").value(USER_ID));

            verify(taskService, times(1)).getTasks(any(TaskFilter.class), any(Pageable.class));
            verifyNoMoreInteractions(taskService);
        }

        @Test
        public void returns200_whenNoFilters() throws Exception
        {
            //GIVEN
            when(taskService.getTasks(any(TaskFilter.class), any(Pageable.class)))
                    .thenReturn(taskResponsePage());

            //WHEN
            mockMvc.perform(get("/tasks")
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize())))


            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(PAGEABLE.getPageNumber()))
                    .andExpect(jsonPath("$.size").value(1))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1))

                    .andExpect(jsonPath("$.content[0].id").value(TASK_ID))
                    .andExpect(jsonPath("$.content[0].name").value(TASK_NAME))
                    .andExpect(jsonPath("$.content[0].status").value(TASK_STATUS.toString()))
                    .andExpect(jsonPath("$.content[0].projectId").value(PROJECT_ID))
                    .andExpect(jsonPath("$.content[0].assigneeId").value(USER_ID));

            verify(taskService, times(1)).getTasks(any(TaskFilter.class), any(Pageable.class));
            verifyNoMoreInteractions(taskService);
        }

        @Test
        public void returns400_whenProjectIdNegative() throws Exception
        {
            //WHEN
            mockMvc.perform(get("/tasks")
                            .param("projectId", "-144"))

            //THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_INVALID"))
                    .andExpect(jsonPath("$.timestamp").exists());

            verifyNoInteractions(taskService);
        }

        @Test
        public void returns400_whenStatusInvalid() throws Exception
        {
            //WHEN
            mockMvc.perform(get("/tasks")
                            .param("status", "NON-EXISTANT-STATUS"))

            //THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("REQUEST_INVALID"))
                    .andExpect(jsonPath("$.timestamp").exists());

            verifyNoInteractions(taskService);
        }
    }
}
