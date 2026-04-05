package taskmanager.utils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class WithMockUserIdSecurityContextFactory
        implements WithSecurityContextFactory<WithMockUserId>
{
    @Override
    public SecurityContext createSecurityContext(WithMockUserId annotation)
    {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        annotation.userId(),
                        null,
                        List.of()
                );

        context.setAuthentication(authentication);
        return context;
    }
}