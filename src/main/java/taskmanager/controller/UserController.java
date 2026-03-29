package taskmanager.controller;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import taskmanager.service.UserService;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController
{
    private final UserService userService;
}
