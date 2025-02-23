package skillchange.be.Controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import skillchange.be.MeetService;
import skillchange.be.auth.SecurityFilter;

@RestController
@RequestMapping("/meets")
@SecurityRequirement(name = "bearerAuth")
public class MeetController {
    private final MeetService meetService;
    private final SecurityFilter securityFilter;

    public MeetController(MeetService meetService, SecurityFilter securityFilter) {
        this.meetService = meetService;
        this.securityFilter = securityFilter;
    }

    @GetMapping(path = "/todayMeets/count")
    public Integer getSkillDetails(HttpServletRequest request) {
        String jwt = securityFilter.recoverToken(request);
        return meetService.numberOfTodayMeetsAUser(jwt);
    }
}
