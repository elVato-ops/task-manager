package taskmanager.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import taskmanager.security.JwtUtils;
import taskmanager.user.User;
import taskmanager.user.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public LoginResponse login(LoginRequest request)
    {
        User user = userRepository.findByName(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword()))
        {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtUtils.generateToken(user.getId(), user.getName());
        return new LoginResponse(token);
    }
}