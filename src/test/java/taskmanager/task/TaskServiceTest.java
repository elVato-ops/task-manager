package taskmanager.task;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import taskmanager.exception.ForbiddenAccessException;
import taskmanager.exception.NotFoundException;
import taskmanager.exception.ValidationException;
import taskmanager.project.ProjectFinder;
import taskmanager.task.dto.TaskResponse;
import taskmanager.task.filter.TaskFilter;
import taskmanager.user.UserFinder;
import taskmanager.utils.AccessGuard;
import taskmanager.utils.TaskMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static taskmanager.TestConstants.*;
import static taskmanager.exception.ResourceType.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest
{
    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserFinder userFinder;

    @Mock
    private ProjectFinder projectFinder;

    @Mock
    private AccessGuard accessGuard;

    @Spy
    private TaskMapper taskMapper;

    @Nested
    class CreateTask
    {
        @Test
        public void returnsTask_whenSuccess()
        {
            //GIVEN
            when(projectFinder.getProject(PROJECT_ID))
                    .thenReturn(project());

            when(userFinder.getUser(USER_ID))
                    .thenReturn(user());

            when(taskRepository.save(any(Task.class)))
                    .thenReturn(task());

            ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

            //WHEN
            TaskResponse task = taskService.createTask(createTaskRequest(), PROJECT_ID, authenticatedUser());

            //THEN
            verify(projectFinder, times(1)).getProject(PROJECT_ID);
            verifyNoMoreInteractions(projectFinder);
            verify(userFinder, times(1)).getUser(USER_ID);
            verifyNoMoreInteractions(userFinder);
            verify(taskRepository, times(1)).save(captor.capture());
            verifyNoMoreInteractions(taskRepository);
            verify(accessGuard, times(1))
                    .verifyRights(authenticatedUser(), project().getOwner().getId(), PROJECT, PROJECT_ID);
            verifyNoMoreInteractions(accessGuard);

            Task value = captor.getValue();
            assertEquals(TASK_NAME, value.getName());
            assertEquals(TASK_STATUS, value.getStatus());
            assertEquals(user().getId(), value.getAssignee().getId());
            assertEquals(project().getId(), value.getProject().getId());

            assertEquals(TASK_ID, task.id());
            assertEquals(TASK_NAME, task.name());
            assertEquals(TASK_STATUS, task.status());
            assertEquals(user().getId(), task.assigneeId());
            assertEquals(project().getId(), task.projectId());
        }

        @Test
        public void returnsTask_whenNullUserId()
        {
            //GIVEN
            when(projectFinder.getProject(PROJECT_ID))
                    .thenReturn(project());

            when(userFinder.getUser(USER_ID))
                    .thenReturn(user());

            when(taskRepository.save(any(Task.class)))
                    .thenReturn(new Task(TASK_NAME, TASK_STATUS, project(), user()));

            ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

            //WHEN
            TaskResponse task = taskService.createTask(createTaskRequest(), PROJECT_ID, authenticatedUser());

            //THEN
            verify(projectFinder, times(1)).getProject(PROJECT_ID);
            verifyNoMoreInteractions(projectFinder);
            verify(userFinder, times(1)).getUser(USER_ID);
            verifyNoMoreInteractions(projectFinder);
            verify(taskRepository, times(1)).save(captor.capture());
            verifyNoMoreInteractions(taskRepository);
            verify(accessGuard, times(1))
                    .verifyRights(authenticatedUser(), project().getOwner().getId(), PROJECT, PROJECT_ID);
            verifyNoMoreInteractions(accessGuard);

            Task value = captor.getValue();
            assertEquals(TASK_NAME, value.getName());
            assertEquals(TASK_STATUS, value.getStatus());
            assertEquals(USER_ID, value.getAssignee().getId());
            assertEquals(PROJECT_ID, value.getProject().getId());

            assertEquals(TASK_NAME, task.name());
            assertEquals(TASK_STATUS, task.status());
            assertEquals(USER_ID, task.assigneeId());
            assertEquals(project().getId(), task.projectId());
        }

        @Test
        public void throwsNotFoundException_whenProjectNotExists()
        {
            //GIVEN
            when(projectFinder.getProject(PROJECT_ID))
                    .thenThrow(new NotFoundException(PROJECT_ID, PROJECT));

            //WHEN /THEN
            assertThrows(NotFoundException.class,
                    () -> taskService.createTask(createTaskRequest(), PROJECT_ID, authenticatedUser()));

            verify(projectFinder, times(1)).getProject(PROJECT_ID);
            verifyNoMoreInteractions(projectFinder);
            verifyNoInteractions(userFinder);
            verifyNoInteractions(taskRepository);
            verifyNoMoreInteractions(accessGuard);
        }

        @Test
        public void throwsNotFoundException_whenUserNotExists()
        {
            //GIVEN
            when(projectFinder.getProject(PROJECT_ID))
                    .thenReturn(project());

            when(userFinder.getUser(USER_ID))
                    .thenThrow(new NotFoundException(USER_ID, USER));

            //WHEN /THEN
            NotFoundException notFoundException = assertThrows(NotFoundException.class,
                    () -> taskService.createTask(createTaskRequest(), PROJECT_ID, authenticatedUser()));

            verify(projectFinder, times(1)).getProject(PROJECT_ID);
            verifyNoMoreInteractions(projectFinder);
            verify(userFinder, times(1)).getUser(USER_ID);
            verifyNoMoreInteractions(userFinder);
            verifyNoInteractions(taskRepository);
            verify(accessGuard, times(1))
                    .verifyRights(authenticatedUser(), project().getOwner().getId(), PROJECT, PROJECT_ID);
            verifyNoMoreInteractions(accessGuard);

            assertEquals(USER, notFoundException.getResource());
            assertEquals(USER_ID, notFoundException.getId());
        }

        @Test
        public void throwsForbiddenAccessException_whenNoRights()
        {
            //GIVEN
            when(projectFinder.getProject(PROJECT_ID))
                    .thenReturn(project());

            doThrow(ForbiddenAccessException.class)
                    .when(accessGuard).verifyRights(authenticatedUser(), project().getOwner().getId(), PROJECT, PROJECT_ID);

            //WHEN /THEN
            assertThrows(ForbiddenAccessException.class,
                    () -> taskService.createTask(createTaskRequest(), PROJECT_ID, authenticatedUser()));

            verify(projectFinder, times(1)).getProject(PROJECT_ID);
            verifyNoMoreInteractions(projectFinder);
            verify(accessGuard, times(1))
                    .verifyRights(authenticatedUser(), project().getOwner().getId(), PROJECT, PROJECT_ID);
            verifyNoMoreInteractions(accessGuard);
            verifyNoInteractions(userFinder);
            verifyNoInteractions(taskRepository);
        }
    }

    @Nested
    class GetTasksForProject
    {
        @Test
        public void returnsTasks_whenSuccess()
        {
            //GIVEN
            when(projectFinder.getProject(PROJECT_ID)).thenReturn(project());

            when(taskRepository.findByProjectId(PROJECT_ID, PAGEABLE))
                    .thenReturn(tasksPage());

            //WHEN
            Page<TaskResponse> tasks = taskService.getTasks(PROJECT_ID, authenticatedUser(), PAGEABLE);

            //THEN
            verify(projectFinder, times(1)).getProject(PROJECT_ID);
            verifyNoMoreInteractions(projectFinder);
            verify(taskRepository, times(1)).findByProjectId(PROJECT_ID, PAGEABLE);
            verifyNoMoreInteractions(taskRepository);
            verify(accessGuard, times(1))
                    .verifyRights(authenticatedUser(), USER_ID, PROJECT, PROJECT_ID);
            verifyNoMoreInteractions(accessGuard);

            TaskResponse task = tasks.get().toList().get(0);
            assertEquals(task().getName(), task.name());
            assertEquals(task().getStatus(), task.status());
            assertEquals(task().getProject().getId(), task.projectId());
            assertEquals(task().getAssignee().getId(), task.assigneeId());
        }

        @Test
        public void throwsNotFoundException_whenProjectNotExists()
        {
            //GIVEN
            doThrow(NotFoundException.class).when(projectFinder).getProject(PROJECT_ID);

            //WHEN
            assertThrows(NotFoundException.class,
                    () -> taskService.getTasks(PROJECT_ID, authenticatedUser(), PAGEABLE));

            //THEN
            verify(projectFinder, times(1)).getProject(PROJECT_ID);
            verifyNoMoreInteractions(projectFinder);
            verifyNoInteractions(taskRepository);
        }

        @Test
        public void throwsForbiddenAccessException_whenNoRights()
        {
            //GIVEN
            when(projectFinder.getProject(PROJECT_ID)).thenReturn(project());

            doThrow(ForbiddenAccessException.class)
                    .when(accessGuard).verifyRights(authenticatedUser(), USER_ID, PROJECT, PROJECT_ID);

            //WHEN
            assertThrows(ForbiddenAccessException.class,
                    () -> taskService.getTasks(PROJECT_ID, authenticatedUser(), PAGEABLE));

            //THEN
            verify(projectFinder, times(1)).getProject(PROJECT_ID);
            verifyNoMoreInteractions(projectFinder);
            verify(accessGuard, times(1))
                    .verifyRights(authenticatedUser(), USER_ID, PROJECT, PROJECT_ID);
            verifyNoMoreInteractions(accessGuard);
            verifyNoInteractions(taskRepository);
        }
    }

    @Nested
    class GetTasks
    {
        @Test
        public void returnsTasks_whenSuccess()
        {
            //GIVEN
            TaskFilter filter = TaskFilter.builder().build();

            when(taskRepository.findAll(ArgumentMatchers.<Specification<Task>>any(), eq(PAGEABLE)))
                    .thenReturn(tasksPage());

            //WHEN
            Page<TaskResponse> tasks = taskService.getTasks(filter, authenticatedUser(), PAGEABLE);

            //THEN
            verify(taskRepository, times(1))
                    .findAll(ArgumentMatchers.<Specification<Task>>any(), eq(PAGEABLE));
            verifyNoMoreInteractions(taskRepository);
            verifyNoInteractions(accessGuard);

            TaskResponse taskResponse = tasks.get().toList().get(0);
            assertEquals(task().getStatus(), taskResponse.status());
            assertEquals(task().getName(), taskResponse.name());
            assertEquals(task().getProject().getId(), taskResponse.projectId());
            assertEquals(task().getAssignee().getId(), taskResponse.assigneeId());
        }

        @Test
        public void throwsForbiddenAccessException_whenNoRights()
        {
            //GIVEN
            TaskFilter filter = TaskFilter.builder()
                    .assigneeId(USER_ID)
                    .build();

            doThrow(ForbiddenAccessException.class)
                    .when(accessGuard).verifyRights(authenticatedUser(), filter.getAssigneeId(), TASK, null);

            //WHEN
            assertThrows(ForbiddenAccessException.class,
                    () -> taskService.getTasks(filter, authenticatedUser(), PAGEABLE));

            //THEN
            verify(accessGuard, times(1))
                    .verifyRights(authenticatedUser(), USER_ID, TASK, null);
            verifyNoMoreInteractions(accessGuard);
            verifyNoInteractions(taskRepository);
        }
    }

    @Nested
    class UpdateTask
    {
        @Test
        public void returnsUpdatedTask_whenSuccess()
        {
            //GIVEN
            when(taskRepository.findById(TASK_ID))
                    .thenReturn(Optional.of(task()));

            when(taskRepository.save(any(Task.class)))
                    .thenReturn(updatedTask());

            //WHEN
            TaskResponse task = taskService.updateStatus(TASK_ID, NEW_TASK_STATUS, authenticatedUser());

            //THEN
            verify(taskRepository, times(1)).findById(TASK_ID);
            verify(taskRepository, times(1)).save(any(Task.class));
            verifyNoMoreInteractions(taskRepository);
            verify(accessGuard, times(1))
                    .verifyRights(authenticatedUser(), USER_ID, PROJECT, PROJECT_ID);
            verifyNoMoreInteractions(accessGuard);

            assertEquals(updatedTask().getId(), task.id());
            assertEquals(updatedTask().getName(), task.name());
            assertEquals(updatedTask().getProject().getId(), task.projectId());
            assertEquals(updatedTask().getAssignee().getId(), task.assigneeId());
            assertEquals(updatedTask().getStatus(), task.status());
        }

        @Test
        public void throwsBadRequestException_whenNullStatus()
        {
            //GIVEN
            when(taskRepository.findById(TASK_ID))
                    .thenReturn(Optional.of(task()));

            //WHEN
            assertThrows(ValidationException.class,
                    () -> taskService.updateStatus(TASK_ID, null, authenticatedUser()));

            //THEN
            verify(taskRepository, times(1)).findById(TASK_ID);
            verifyNoMoreInteractions(taskRepository);
            verify(accessGuard, times(1))
                    .verifyRights(authenticatedUser(), USER_ID, PROJECT, PROJECT_ID);
            verifyNoMoreInteractions(accessGuard);

        }

        @Test
        public void throwsNotFoundException_whenNotExists()
        {
            //GIVEN
            when(taskRepository.findById(TASK_ID))
                    .thenReturn(Optional.empty());

            //WHEN
            assertThrows(NotFoundException.class,
                    () -> taskService.updateStatus(TASK_ID, NEW_TASK_STATUS, authenticatedUser()));

            //THEN
            verify(taskRepository, times(1)).findById(TASK_ID);
            verifyNoMoreInteractions(taskRepository);
            verifyNoInteractions(accessGuard);
        }

        @Test
        public void throwsForbiddenAccessException_whenNoRights()
        {
            //GIVEN
            when(taskRepository.findById(TASK_ID))
                    .thenReturn(Optional.of(task()));

            doThrow(ForbiddenAccessException.class)
                    .when(accessGuard).verifyRights(authenticatedUser(), USER_ID, PROJECT, PROJECT_ID);

            //WHEN
            assertThrows(ForbiddenAccessException.class,
                    () -> taskService.updateStatus(TASK_ID, NEW_TASK_STATUS, authenticatedUser()));

            //THEN
            verify(taskRepository, times(1)).findById(TASK_ID);
            verifyNoMoreInteractions(taskRepository);
            verify(accessGuard, times(1))
                    .verifyRights(authenticatedUser(), USER_ID, PROJECT, PROJECT_ID);
            verifyNoMoreInteractions(accessGuard);
        }
    }
}