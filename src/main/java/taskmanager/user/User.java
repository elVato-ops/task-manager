package taskmanager.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import taskmanager.exception.ValidationException;

import static taskmanager.exception.ResourceType.USER;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    public User(String name, String password)
    {
        if (!StringUtils.hasText(name))
        {
            throwValidation("User name must not be empty");
        }

        if (!StringUtils.hasText(password))
        {
            throwValidation("Password must not be empty");
        }

        this.name = name;
        this.password = password;
    }

    public void throwValidation(String message)
    {
        throw new ValidationException(message, USER);
    }
}