package taskmanager.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import taskmanager.exception.ValidationException;

import static taskmanager.exception.ErrorCode.PROJECT_DATA_INVALID;

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
            validationException("Project name must not be empty");
        }

        if (owner == null)
        {
            validationException("Project owner must not be null");
        }

        this.name = name;
        this.owner = owner;
    }

    public void validationException(String message)
    {
        throw new ValidationException(message, PROJECT_DATA_INVALID);
    }
}