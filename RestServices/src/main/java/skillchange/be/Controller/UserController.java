package skillchange.be.Controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;
import skillchange.be.UserService;
import skillchange.be.dto.*;


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
        userService.register(userCreateDto);
    }

    @PostMapping("/send-mail")
    public void sendMail(@RequestBody MailDto mailDto) {
        userService.sendResetTokenMail(mailDto.getEmail());
    }

    @PostMapping("/verify-reset-token")
    public void verifyResetToken(@RequestBody ResetTokenValidateDto resetTokenValidateDto) {
        userService.validateResetToken(resetTokenValidateDto);
    }

    @PutMapping("/reset-password")
    public void resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        userService.resetPassword(resetPasswordDto);
    }

    @GetMapping("")
    public UserViewDto getProfileData() {
        return userService.getProfileData("");
    }
}
