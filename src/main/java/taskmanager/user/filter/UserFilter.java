package taskmanager.user.filter;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
public class UserFilter
{
    private final String name;
    private final Instant fromCreationDate;
}
