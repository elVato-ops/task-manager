package taskmanager;

import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import taskmanager.project.Project;
import taskmanager.project.dto.CreateProjectRequest;
import taskmanager.user.User;

import java.lang.reflect.Field;
import java.util.List;

public class TestConstants
{
    public static final String USER_NAME = "Bobek";
    public static final String PASSWORD = "password";
    public static final String PROJECT_NAME = "Some project";

    public static final Long USER_ID = 17L;
    public static final Long PROJECT_ID = 93L;

    public static final Pageable PAGEABLE = PageRequest.of(0, 10);

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

    public static CreateProjectRequest createProjectRequest()
    {
        return new CreateProjectRequest(PROJECT_NAME, USER_ID);
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
}
