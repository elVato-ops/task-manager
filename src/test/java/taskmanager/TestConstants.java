package taskmanager;

import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import taskmanager.project.Project;
import taskmanager.project.dto.CreateProjectRequest;
import taskmanager.project.dto.ProjectResponse;
import taskmanager.task.Task;
import taskmanager.task.TaskStatus;
import taskmanager.task.dto.CreateTaskRequest;
import taskmanager.task.dto.TaskResponse;
import taskmanager.user.User;
import taskmanager.user.dto.CreateUserRequest;
import taskmanager.user.dto.UserResponse;

import java.lang.reflect.Field;
import java.util.List;

public class TestConstants
{
    public static final String USER_NAME = "Bobek";
    public static final String OTHER_USER_NAME = "Dudek";
    public static final String PASSWORD = "password";
    public static final String PROJECT_NAME = "Some project";
    public static final String TASK_NAME = "Some task";

    public static final Long USER_ID = 17L;
    public static final Long OTHER_USER_ID = 37L;
    public static final Long PROJECT_ID = 93L;
    public static final Long TASK_ID = 46L;

    public static final TaskStatus TASK_STATUS = TaskStatus.TODO;

    public static final Pageable PAGEABLE = PageRequest.of(0, 10);

    public static final String INSTANT_STRING = "2024-01-15T10:30:00Z";

    @SneakyThrows
    public static Project project()
    {
        Project project = new Project(PROJECT_NAME, user());

        Field field = Project.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(project, PROJECT_ID);

        return project;
    }

    public static Page<Project> projectsPage()
    {
        return new PageImpl<>(List.of(project()));
    }

    public static ProjectResponse projectResponse()
    {
        return new ProjectResponse(PROJECT_ID, PROJECT_NAME, USER_ID);
    }

    public static Page<ProjectResponse> projectResponsePage()
    {
        return new PageImpl<>(List.of(projectResponse()));
    }

    public static CreateProjectRequest createProjectRequest()
    {
        return new CreateProjectRequest(PROJECT_NAME, USER_ID);
    }

    public static CreateUserRequest createUserRequest()
    {
        return new CreateUserRequest(USER_NAME, PASSWORD);
    }

    @SneakyThrows
    public static User user()
    {
        User user = new User(USER_NAME, PASSWORD);

        Field field = User.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(user, USER_ID);

        return user;
    }

    public static Page<User> usersPage()
    {
        return new PageImpl<>(List.of(user()));
    }

    public static UserResponse userResponse()
    {
        return new UserResponse(USER_ID, USER_NAME);
    }

    public static UserResponse otherUserResponse()
    {
        return new UserResponse(OTHER_USER_ID, OTHER_USER_NAME);
    }

    public static Page<UserResponse> usersResponsePage()
    {
        return new PageImpl<>(List.of(userResponse(), otherUserResponse()));
    }

    @SneakyThrows
    public static Task task()
    {
        Task task = new Task(TASK_NAME, TASK_STATUS, project(), user());

        Field field = Task.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(task, TASK_ID);

        return task;
    }

    public static CreateTaskRequest createTaskRequest()
    {
        return new CreateTaskRequest(TASK_NAME, USER_ID);
    }

    public static Page<Task> tasksPage()
    {
        return new PageImpl<>(List.of(task()));
    }

    public static TaskResponse taskResponse()
    {
        return new TaskResponse(TASK_ID, TASK_NAME, TASK_STATUS, PROJECT_ID, USER_ID);
    }

    public static Page<TaskResponse> taskResponsePage()
    {
        return new PageImpl<>(List.of(taskResponse()));
    }
}
