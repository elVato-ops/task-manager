package taskmanager.utils;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static taskmanager.TestConstants.USER_ID;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUserIdSecurityContextFactory.class)
public @interface WithMockUserId
{
    long userId() default USER_ID;
}