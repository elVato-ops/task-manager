package taskmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import taskmanager.auth.JwtAuthFilter;
import taskmanager.auth.JwtUtils;
import taskmanager.utils.WithMockUserId;

@WithMockUserId
public class BaseControllerTest
{
    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    protected JwtAuthFilter jwtAuthFilter;
}
