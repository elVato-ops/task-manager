package taskmanager.task.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import specification.SpecificationBuilder;
import taskmanager.task.Task;
import taskmanager.task.TaskStatus;
import taskmanager.task.filter.TaskFilter;

public class TaskSpecification
{
    public static Specification<Task> withFilter(TaskFilter filter)
    {
        String name = filter.getName();
        TaskStatus status = filter.getStatus();
        Long assigneeId = filter.getAssigneeId();
        Long projectId = filter.getProjectId();

        return new SpecificationBuilder<Task>()
                .and(StringUtils.hasText(name), hasName(name))
                .and(status != null, hasStatus(status))
                .and(assigneeId != null, isForAssignee(assigneeId))
                .and(projectId != null, isForProject(projectId))
                .build();
    }

    private static Specification<Task> hasName(String name)
    {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<Task> hasStatus(TaskStatus status)
    {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    private static Specification<Task> isForAssignee(Long assigneeId)
    {
        return (root, query, cb) ->
                cb.equal(root.get("user").get("id"), assigneeId);
    }

    private static Specification<Task> isForProject(Long projectId)
    {
        return (root, query, cb) ->
                cb.equal(root.get("project").get("id"), projectId);
    }
}