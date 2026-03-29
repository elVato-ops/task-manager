package taskmanager.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import taskmanager.exception.ValidationException;

import static taskmanager.exception.ErrorCode.TASK_DATA_INVALID;

@Entity
@Table(name = "tasks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User assignee;

    public Task(String name, TaskStatus status, Project project, User assignee)
    {
        if (!StringUtils.hasText(name))
        {
            validationException("Task name must not be empty");
        }

        if (status == null)
        {
            validationException("Task status must not be null");
        }

        if (project == null)
        {
            validationException("Task project must not be null");
        }

        if (assignee == null)
        {
            validationException("Task assignee must not be null");
        }

        this.name = name;
        this.status = status;
        this.project = project;
        this.assignee = assignee;
    }

    public void validationException(String message)
    {
        throw new ValidationException(message, TASK_DATA_INVALID);
    }
}