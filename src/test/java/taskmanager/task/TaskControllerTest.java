package taskmanager.task;


import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import taskmanager.BaseControllerTest;
import taskmanager.exception.NotFoundException;
import taskmanager.task.filter.TaskFilter;
import taskmanager.utils.WithMockUserId;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static taskmanager.TestConstants.*;
import static taskmanager.exception.ErrorCode.NOT_FOUND;
import static taskmanager.exception.ErrorCode.REQUEST_INVALID;
import static taskmanager.exception.ResourceType.TASK;
import static taskmanager.task.TaskStatus.IN_PROGRESS;

@WebMvcTest(TaskController.class)
@WithMockUserId
public class TaskControllerTest extends BaseControllerTest
{
    @MockBean
    private TaskService taskService;

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
                    .andExpect(jsonPath("$.errorCode").value(REQUEST_INVALID.toString()))
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
                    .andExpect(jsonPath("$.errorCode").value(REQUEST_INVALID.toString()))
                    .andExpect(jsonPath("$.timestamp").exists());

            verifyNoInteractions(taskService);
        }
    }
    
    @Nested
    class UpdateTask
    {
        @Test
        public void returns200_whenSuccess() throws Exception
        {
            //GIVEN
            when(taskService.updateStatus(TASK_ID, NEW_TASK_STATUS))
                    .thenReturn(updatedTaskResponse());

            //WHEN
            mockMvc.perform(patch("/tasks/" + TASK_ID + "/status/" + IN_PROGRESS))

            //THEN
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(updatedTaskResponse().id()))
                    .andExpect(jsonPath("$.name").value(updatedTaskResponse().name()))
                    .andExpect(jsonPath("$.status").value(updatedTaskResponse().status().toString()))
                    .andExpect(jsonPath("$.projectId").value(updatedTaskResponse().projectId()))
                    .andExpect(jsonPath("$.assigneeId").value(updatedTaskResponse().assigneeId()));
        }

        @Test
        public void returns400_whenIncorrectStatus() throws Exception
        {
            //WHEN
            mockMvc.perform(patch("/tasks/" + TASK_ID + "/status/incorrectStatus)"))

            //THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value(REQUEST_INVALID.toString()))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        public void returns404_whenTaskNotExists() throws Exception
        {
            //GIVEN
            when(taskService.updateStatus(TASK_ID, NEW_TASK_STATUS))
                    .thenThrow(new NotFoundException(TASK_ID, TASK));

            //WHEN
            mockMvc.perform(patch("/tasks/" + TASK_ID + "/status/" + NEW_TASK_STATUS))

            //THEN
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resource").value(TASK.toString()))
                    .andExpect(jsonPath("$.errorCode").value(NOT_FOUND.toString()))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }
}