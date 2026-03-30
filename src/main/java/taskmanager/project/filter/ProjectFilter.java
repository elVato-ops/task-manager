package taskmanager.project.filter;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProjectFilter
{
    private final String name;
    private final Long ownerId;
}
