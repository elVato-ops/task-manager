package taskmanager.task;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import taskmanager.auth.AuthService;
import taskmanager.exception.ForbiddenAccessException;
import taskmanager.exception.NotFoundException;
import taskmanager.exception.ValidationException;
import taskmanager.project.ProjectFinder;
import taskmanager.task.dto.TaskResponse;
import taskmanager.task.filter.TaskFilter;
import taskmanager.user.UserFinder;
import taskmanager.utils.TaskMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static taskmanager.TestConstants.*;
import static taskmanager.exception.ResourceType.PROJECT;
import static taskmanager.exception.ResourceType.USER;

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
    private AuthService authService;

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
            TaskResponse task = taskService.createTask(createTaskRequest(), PROJECT_ID, USER_ID);

            //THEN
            verify(projectFinder, times(1)).getProject(PROJECT_ID);
            verifyNoMoreInteractions(projectFinder);
            verify(userFinder, times(1)).getUser(USER_ID);
            verifyNoMoreInteractions(userFinder);
            verify(taskRepository, times(1)).save(captor.capture());
            verifyNoMoreInteractions(taskRepository);

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
            TaskResponse task = taskService.createTask(createTaskRequest(), PROJECT_ID, USER_ID);

            //THEN
            verify(projectFinder, times(1)).getProject(PROJECT_ID);
            verifyNoMoreInteractions(projectFinder);
            verify(userFinder, times(1)).getUser(USER_ID);
            verifyNoMoreInteractions(projectFinder);
            verify(taskRepository, times(1)).save(captor.capture());
            verifyNoMoreInteractions(taskRepository);

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
                    () -> taskService.createTask(createTaskRequest(), PROJECT_ID, USER_ID));

            verify(projectFinder, times(1)).getProject(PROJECT_ID);
            verifyNoMoreInteractions(projectFinder);
            verifyNoInteractions(userFinder);
            verifyNoInteractions(taskRepository);
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
                    () -> taskService.createTask(createTaskRequest(), PROJECT_ID, USER_ID));

            verify(projectFinder, times(1)).getProject(PROJECT_ID);
            verifyNoMoreInteractions(projectFinder);
            verify(userFinder, times(1)).getUser(USER_ID);
            verifyNoMoreInteractions(userFinder);
            verifyNoInteractions(taskRepository);

            assertEquals(USER, notFoundException.getResource());
            assertEquals(USER_ID, notFoundException.getId());
        }

        @Test
        public void throwsForbiddenAccessException_whenNotProjectOwnerNotAdmin()
        {
            //GIVEN
            when(projectFinder.getProject(PROJECT_ID))
                    .thenReturn(project());

            doThrow(ForbiddenAccessException.class)
                    .when(authService).verifyAdminRole(OTHER_USER_ID);

            //WHEN /THEN
            assertThrows(ForbiddenAccessException.class,
                    () -> taskService.createTask(createTaskRequest(), PROJECT_ID, OTHER_USER_ID));

            verify(projectFinder, times(1)).getProject(PROJECT_ID);
            verifyNoMoreInteractions(projectFinder);
            verify(authService, times(1)).verifyAdminRole(OTHER_USER_ID);
            verifyNoMoreInteractions(authService);
            verifyNoInteractions(userFinder);
            verifyNoInteractions(taskRepository);
        }

        @Test
        public void returnsTask_whenNotProjectOwnerButAdmin()
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
            TaskResponse task = taskService.createTask(createTaskRequest(), PROJECT_ID, OTHER_USER_ID);

            //THEN
            verify(projectFinder, times(1)).getProject(PROJECT_ID);
            verifyNoMoreInteractions(projectFinder);
            verify(userFinder, times(1)).getUser(USER_ID);
            verifyNoMoreInteractions(userFinder);
            verify(taskRepository, times(1)).save(captor.capture());
            verifyNoMoreInteractions(taskRepository);

            Task value = captor.getValue();
            assertEquals(TASK_NAME, value.getName());
            assertEquals(TASK_STATUS, value.getStatus());
            assertEquals(user().getId(), value.getAssignee().getId());
            assertEquals(project().getId(), value.getProject().getId());

            assertEquals(TASK_NAME, task.name());
            assertEquals(TASK_STATUS, task.status());
            assertEquals(user().getId(), task.assigneeId());
            assertEquals(project().getId(), task.projectId());
        }
    }

    @Nested
    class GetTasksForProject
    {
        @Test
        public void returnsTasks_whenSuccess()
        {
            //GIVEN
            when(projectFinder.existsById(PROJECT_ID))
                    .thenReturn(true);

            when(taskRepository.findByProjectId(PROJECT_ID, PAGEABLE))
                    .thenReturn(tasksPage());

            //WHEN
            Page<TaskResponse> tasks = taskService.getTasks(PROJECT_ID, PAGEABLE);

            //THEN
            verify(projectFinder, times(1)).existsById(PROJECT_ID);
            verifyNoMoreInteractions(projectFinder);
            verify(taskRepository, times(1)).findByProjectId(PROJECT_ID, PAGEABLE);
            verifyNoMoreInteractions(taskRepository);

            TaskResponse task = tasks.get().toList().get(0);
            assertEquals(task().getName(), task.name());
            assertEquals(task().getStatus(), task.status());
            assertEquals(task().getProject().getId(), task.projectId());
            assertEquals(task().getAssignee().getId(), task.assigneeId());
        }

        @Test
        public void throwsNotFound_whenProjectNotExists()
        {
            //GIVEN
            when(projectFinder.existsById(PROJECT_ID))
                    .thenReturn(false);

            //WHEN
            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> taskService.getTasks(PROJECT_ID, PAGEABLE));

            //THEN
            verify(projectFinder, times(1)).existsById(PROJECT_ID);
            verifyNoMoreInteractions(projectFinder);
            verifyNoInteractions(taskRepository);

            assertEquals(PROJECT_ID, exception.getId());
            assertEquals(PROJECT, exception.getResource());
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
            Page<TaskResponse> tasks = taskService.getTasks(filter, PAGEABLE);

            //THEN
            verify(taskRepository, times(1))
                    .findAll(ArgumentMatchers.<Specification<Task>>any(), eq(PAGEABLE));

            verifyNoMoreInteractions(taskRepository);

            TaskResponse taskResponse = tasks.get().toList().get(0);
            assertEquals(task().getStatus(), taskResponse.status());
            assertEquals(task().getName(), taskResponse.name());
            assertEquals(task().getProject().getId(), taskResponse.projectId());
            assertEquals(task().getAssignee().getId(), taskResponse.assigneeId());
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
            TaskResponse task = taskService.updateStatus(TASK_ID, NEW_TASK_STATUS);

            //THEN
            verify(taskRepository, times(1)).findById(TASK_ID);
            verify(taskRepository, times(1)).save(any(Task.class));
            verifyNoMoreInteractions(taskRepository);

            assertEquals(updatedTask().getId(), task.id());
            assertEquals(updatedTask().getName(), task.name());
            assertEquals(updatedTask().getProject().getId(), task.projectId());
            assertEquals(updatedTask().getAssignee().getId(), task.assigneeId());
            assertEquals(updatedTask().getStatus(), task.status());
        }

        @Test
        public void throws400_whenNullStatus()
        {
            //GIVEN
            when(taskRepository.findById(TASK_ID))
                    .thenReturn(Optional.of(task()));

            //WHEN
            assertThrows(ValidationException.class,
                    () -> taskService.updateStatus(TASK_ID, null));

            //THEN
            verify(taskRepository, times(1)).findById(TASK_ID);
            verifyNoMoreInteractions(taskRepository);
        }

        @Test
        public void throws404_whenNotExists()
        {
            //GIVEN
            when(taskRepository.findById(TASK_ID))
                    .thenReturn(Optional.empty());

            //WHEN
            assertThrows(NotFoundException.class,
                    () -> taskService.updateStatus(TASK_ID, NEW_TASK_STATUS));

            //THEN
            verify(taskRepository, times(1)).findById(TASK_ID);
            verifyNoMoreInteractions(taskRepository);
        }
    }
}