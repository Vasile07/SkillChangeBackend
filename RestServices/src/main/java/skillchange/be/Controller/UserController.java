package skillchange.be.Controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import skillchange.be.UserService;
import skillchange.be.auth.SecurityFilter;
import skillchange.be.dto.*;

import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;
    private final SecurityFilter securityFilter;

    public UserController(UserService userService, SecurityFilter securityFilter) {
        this.userService = userService;
        this.securityFilter = securityFilter;
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

    @GetMapping({"", "/{userId}"})
    public UserViewDto getProfileData(
            @PathVariable(required = false) Integer userId,
            HttpServletRequest request) {

        String token = securityFilter.recoverToken(request);

        if (userId != null) {
            return userService.getProfileData(userId);
        } else {
            return userService.getProfileData(token);
        }
    }

    @GetMapping("/myFriends")
    public List<UserViewDto> getMyFriends(HttpServletRequest request) {
        String jwt = securityFilter.recoverToken(request);
        return userService.getMyFriends(jwt);
    }

    @PostMapping("/follow")
    public void follow(@RequestBody Integer followingUserId, HttpServletRequest request) {
        String jwt = securityFilter.recoverToken(request);
        userService.follow(jwt, followingUserId);
    }

    @GetMapping({"/followers", "/followers/{userId}"})
    public List<UserViewDto> getFollowers(@PathVariable(required = false) Integer userId, HttpServletRequest request) {
        String token = securityFilter.recoverToken(request);

        if (userId != null) {
            return userService.getFollowers(userId);
        } else {
            return userService.getFollowers(token);
        }
    }
}
