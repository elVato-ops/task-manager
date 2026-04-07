package taskmanager.project;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import taskmanager.exception.NotFoundException;
import taskmanager.project.filter.ProjectFilter;
import taskmanager.project.specification.ProjectSpecification;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static taskmanager.TestConstants.*;
import static taskmanager.exception.ResourceType.PROJECT;

@ExtendWith(MockitoExtension.class)
public class ProjectFinderTest
{
    @InjectMocks
    private ProjectFinder projectFinder;

    @Mock
    private ProjectRepository projectRepository;

    @Nested
    class GetProject
    {
        @Test
        public void returnsProject_whenSuccess()
        {
            //GIVEN
            when(projectRepository.findById(PROJECT_ID))
                    .thenReturn(Optional.of(project()));

            //WHEN
            Project project = projectFinder.getProject(PROJECT_ID);

            //THEN
             verify(projectRepository, times(1)).findById(PROJECT_ID);
             verifyNoMoreInteractions(projectRepository);

             assertEquals(project().getId(), project.getId());
             assertEquals(project().getName(), project.getName());
             assertEquals(project().getOwner().getId(), project.getOwner().getId());
        }

        @Test
        public void throwsNotFoundException_whenNotExists()
        {
            //GIVEN
            when(projectRepository.findById(PROJECT_ID))
                    .thenReturn(Optional.empty());

            //WHEN /THEN
            NotFoundException notFoundException =
                    assertThrows(NotFoundException.class, () -> projectFinder.getProject(PROJECT_ID));

            verify(projectRepository, times(1)).findById(PROJECT_ID);
            verifyNoMoreInteractions(projectRepository);

            assertEquals(PROJECT, notFoundException.getResource());
            assertEquals(PROJECT_ID, notFoundException.getId());
        }
    }

    @Nested
    class GetProjects
    {
        @Test
        public void returnsProjects_whenSuccess()
        {
            ProjectFilter filter = ProjectFilter.builder().build();
            Specification<Project> specification = ProjectSpecification.withFilter(filter);

            //GIVEN
            when(projectRepository.findAll(specification, PAGEABLE))
                    .thenReturn(projectsPage());

            //WHEN
            Page<Project> projects = projectFinder.getProjects(specification, PAGEABLE);

            //THEN
            verify(projectRepository, times(1))
                    .findAll(specification, PAGEABLE);

            verifyNoMoreInteractions(projectRepository);

            assertEquals(1, projects.getTotalElements());

            Project project = projects.get().toList().get(0);
            assertEquals(project().getId(), project.getId());
            assertEquals(project().getName(), project.getName());
            assertEquals(project().getOwner().getId(), project.getOwner().getId());
        }
    }

    @Nested
    class ExistsById
    {
        @Test
        public void returnsTrue_whenExists()
        {
            //GIVEN
            when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);

            //WHEN
            boolean result = projectFinder.existsById(PROJECT_ID);

            //THEN
            verify(projectRepository, times(1))
                    .existsById(PROJECT_ID);

            verifyNoMoreInteractions(projectRepository);

            assertTrue(result);
        }

        @Test
        public void returnsFalse_whenNotExists()
        {
            //GIVEN
            when(projectRepository.existsById(USER_ID)).thenReturn(false);

            //WHEN
            boolean result = projectFinder.existsById(USER_ID);

            //THEN
            verify(projectRepository, times(1))
                    .existsById(USER_ID);

            verifyNoMoreInteractions(projectRepository);

            assertFalse(result);
        }
    }
}
