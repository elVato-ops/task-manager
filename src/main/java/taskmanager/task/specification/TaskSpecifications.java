package taskmanager.task.specification;

import org.springframework.data.jpa.domain.Specification;
import taskmanager.task.Task;
import taskmanager.task.TaskStatus;

public class TaskSpecifications
{
    public static Specification<Task> hasName(String name)
    {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Task> hasStatus(TaskStatus status)
    {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    public static Specification<Task> isForAssignee(Long assigneeId)
    {
        return (root, query, cb) ->
                cb.equal(root.get("user").get("id"), assigneeId);
    }

    public static Specification<Task> isForProject(Long projectId)
    {
        return (root, query, cb) ->
                cb.equal(root.get("project").get("id"), projectId);

    }
}