package skillchange.be.Controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import skillchange.be.MeetService;
import skillchange.be.auth.SecurityFilter;
import skillchange.be.dto.FutureMeetDetails;
import skillchange.be.dto.MeetCreateDto;
import skillchange.be.dto.TodayMeetDetail;

import java.util.List;

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

    @PostMapping(path = "")
    public void addMeet(@RequestBody MeetCreateDto meetCreateDto, HttpServletRequest request) {
        System.out.println(meetCreateDto);
        String jwt = securityFilter.recoverToken(request);
        meetService.addMeet(meetCreateDto, jwt);
    }

    @GetMapping(path = "/todayMeets/count")
    public Integer getNumberOfTodayMeets(HttpServletRequest request) {
        String jwt = securityFilter.recoverToken(request);
        return meetService.numberOfTodayMeetsAUser(jwt);
    }

    @GetMapping(path = "/todayMeets")
    public List<TodayMeetDetail> getTodayMeets(HttpServletRequest request) {
        String jwt = securityFilter.recoverToken(request);
        return meetService.getTodayMeets(jwt);
    }

    @GetMapping({"/totalMeets", "/totalMeets/{userId}"})
    public Integer getTotalMeets(@PathVariable(required = false) Integer userId, HttpServletRequest request) {
        String token = securityFilter.recoverToken(request);

        if (userId != null) {
            return meetService.getTotalMeets(userId);
        } else {
            return meetService.getTotalMeets(token);
        }
    }

    @GetMapping("/futureMeets")
    public List<FutureMeetDetails> getFutureMeets(HttpServletRequest request) {
        String token = securityFilter.recoverToken(request);
        return meetService.getFutureMeets(token);
    }
}
