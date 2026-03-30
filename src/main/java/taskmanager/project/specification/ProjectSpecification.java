package taskmanager.project.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import specification.SpecificationBuilder;
import taskmanager.project.Project;
import taskmanager.project.filter.ProjectFilter;

public class ProjectSpecification
{
    public static Specification<Project> withFilter(ProjectFilter filter)
    {
        String name = filter.getName();
        Long ownerId = filter.getOwnerId();

        return new SpecificationBuilder<Project>()
                .and(StringUtils.hasText(name), hasName(name))
                .and(ownerId != null, hasOwnerId(ownerId))
                .build();
    }

    private static Specification<Project> hasName(String name)
    {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<Project> hasOwnerId(Long ownerId)
    {
        return (root, query, cb) ->
                cb.equal(root.get("owner").get("id"), ownerId);
    }
}
