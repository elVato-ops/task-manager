package taskmanager.user.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import taskmanager.specification.SpecificationBuilder;
import taskmanager.user.User;
import taskmanager.user.UserRole;
import taskmanager.user.filter.UserFilter;

import java.time.Instant;

public class UserSpecification
{
    public static Specification<User> withFilter(UserFilter filter)
    {
        String name = filter.getName();
        Instant fromCreationDate = filter.getFromCreationDate();
        UserRole role = filter.getUserRole();

        return new SpecificationBuilder<User>()
                .and(StringUtils.hasText(name), hasName(name))
                .and(fromCreationDate != null, fromCreationDate(fromCreationDate))
                .and(role != null, hasRole(role))
                .build();
    }

    private static Specification<User> hasName(String name)
    {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<User> fromCreationDate(Instant fromCreationDate)
    {
        return (root, query, cb) ->
                cb.greaterThan(root.get("creationDate"), fromCreationDate);
    }

    private static Specification<User> hasRole(UserRole role)
    {
        return (root, query, cb) ->
                cb.equal(root.get("role"), role);
    }
}