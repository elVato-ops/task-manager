package taskmanager.project;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import taskmanager.exception.NotFoundException;
import taskmanager.project.dto.ProjectResponse;
import taskmanager.project.filter.ProjectFilter;
import taskmanager.user.UserFinder;
import taskmanager.utils.ProjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static taskmanager.TestConstants.*;
import static taskmanager.exception.ResourceType.PROJECT;
import static taskmanager.exception.ResourceType.USER;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest
{
    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectFinder projectFinder;

    @Mock
    private UserFinder userFinder;

    @Spy
    private ProjectMapper projectMapper;

    @Nested
    class CreateProject
    {
        @Test
        public void returnsProject_whenSuccess()
        {
            //GIVEN
            when(userFinder.getUser(USER_ID)).thenReturn(user());
            when(projectRepository.save(any(Project.class)))
                    .thenReturn(project());

            ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);

            //WHEN
            ProjectResponse projectResponse = projectService.createProject(createProjectRequest(), USER_ID);

            //THEN
            verify(userFinder, times(1)).getUser(USER_ID);
            verifyNoMoreInteractions(userFinder);
            verify(projectRepository, times(1)).save(captor.capture());
            verifyNoMoreInteractions(projectRepository);

            Project value = captor.getValue();
            assertEquals(project().getName(), value.getName());
            assertEquals(project().getOwner().getId(), value.getOwner().getId());

            assertEquals(project().getName(), projectResponse.name());
        }

        @Test
        public void throwsNotFoundException_whenUserNotExists()
        {
            //GIVEN
            when(userFinder.getUser(USER_ID))
                    .thenThrow(new NotFoundException(USER_ID, USER));

            //WHEN / THEN
            assertThrows(NotFoundException.class,
                    () -> projectService.createProject(createProjectRequest(), USER_ID));

            verify(userFinder, times(1)).getUser(USER_ID);
            verifyNoMoreInteractions(userFinder);
            verifyNoInteractions(projectRepository);
        }
    }

    @Nested
    class GetProject
    {
        @Test
        public void returnsProject_whenExists()
        {
            //GIVEN
            when(projectFinder.getProject(PROJECT_ID))
                    .thenReturn(project());

            //WHEN
            ProjectResponse response = projectService.getProject(PROJECT_ID);

            //THEN
            verify(projectFinder, times(1)).getProject(PROJECT_ID);
            verifyNoMoreInteractions(projectFinder);

            assertEquals(project().getId(), response.id());
            assertEquals(project().getName(), response.name());
            assertEquals(project().getOwner().getId(), response.ownerId());
        }

        @Test
        public void throwsNotFoundException_whenNotExists()
        {
            //GIVEN
            when(projectFinder.getProject(PROJECT_ID))
                    .thenThrow(new NotFoundException(PROJECT_ID, PROJECT));

            //WHEN /THEN
            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> projectService.getProject(PROJECT_ID));

            verify(projectFinder, times(1)).getProject(PROJECT_ID);
            verifyNoMoreInteractions(projectFinder);

            assertEquals(project().getId(), exception.getId());
            assertEquals(PROJECT, exception.getResource());
        }
    }

    @Nested
    class GetProjects
    {
        @Test
        public void returnsFilteredProjects_whenSuccess()
        {
            //GIVEN
            ProjectFilter filter = ProjectFilter.builder().build();
            when(projectFinder.getProjects(ArgumentMatchers.any(), eq(PAGEABLE)))
                    .thenReturn(projectsPage());

            //WHEN
            Page<ProjectResponse> response = projectService
                    .getProjects(filter, PAGEABLE);

            //THEN
            verify(projectFinder, times(1))
                    .getProjects(ArgumentMatchers.any(), eq(PAGEABLE));
            verifyNoMoreInteractions(projectFinder);

            assertEquals(1, response.getTotalElements());

            ProjectResponse project = response.get().toList().get(0);
            assertEquals(project().getId(), project.id());
            assertEquals(project().getName(), project.name());
            assertEquals(project().getOwner().getId(), project.ownerId());
        }
    }
}
