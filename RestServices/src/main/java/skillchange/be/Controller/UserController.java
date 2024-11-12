package skillchange.be.Controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import skillchange.be.UserService;
import skillchange.be.dto.JwtDto;
import skillchange.be.dto.LoginDto;
import skillchange.be.dto.UserCreateDto;


@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public JwtDto login(@RequestBody LoginDto loginDto) {
        return userService.login(loginDto);
    }

    @PostMapping("/register")
    public void register(@RequestBody UserCreateDto userCreateDto) {
        userService.save(userCreateDto);
    }
}
