package taskmanager;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import taskmanager.auth.JwtAuthFilter;
import taskmanager.auth.JwtUtils;
import taskmanager.config.TestSecurityConfig;
import taskmanager.utils.WithMockUserId;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@Import(TestSecurityConfig.class)
@WithMockUserId
public class BaseControllerTest
{
    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    protected JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void setup() throws Exception {
        doAnswer(invocation -> {
            invocation.getArgument(2, FilterChain.class).doFilter(
                    invocation.getArgument(0, ServletRequest.class),
                    invocation.getArgument(1, ServletResponse.class)
            );
            return null;
        }).when(jwtAuthFilter).doFilter(any(), any(), any());
    }
}
