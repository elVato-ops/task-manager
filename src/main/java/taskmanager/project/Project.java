package taskmanager.project;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import taskmanager.exception.ValidationException;
import taskmanager.user.User;

import static taskmanager.exception.ResourceType.PROJECT;

@Entity
@Table(name = "projects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    public Project(String name, User owner)
    {
        if (!StringUtils.hasText(name))
        {
            throwValidation("Project name must not be empty");
        }

        if (owner == null)
        {
            throwValidation("Project owner must not be null");
        }

        this.name = name;
        this.owner = owner;
    }

    private void throwValidation(String message)
    {
        throw new ValidationException(message, PROJECT);
    }
}