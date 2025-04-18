package skillchange.be.Controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import skillchange.be.SkillService;
import skillchange.be.auth.SecurityFilter;
import skillchange.be.dto.SkillDetailsDto;
import skillchange.be.dto.SkillViewDto;

import java.util.List;


@RestController
@RequestMapping("/skills")
@SecurityRequirement(name = "bearerAuth")
public class SkillController {

    private final SkillService skillService;
    private final SecurityFilter securityFilter;

    public SkillController(SkillService skillService, SecurityFilter securityFilter) {
        this.skillService = skillService;
        this.securityFilter = securityFilter;
    }

    @GetMapping(path = "")
    public List<SkillViewDto> getAllSkills() {
        return skillService.getAllSkills();
    }

    @GetMapping("/byType")
    public List<SkillViewDto> getSkillsByType(@RequestParam(required = false) String type) {
        if (type != null)
            return skillService.getSkillsByType(type);
        else
            return skillService.getAllSkills();
    }

    @GetMapping(path = "/mySkills")
    public List<SkillViewDto> getYourSkills(HttpServletRequest request) {
        String jwt = securityFilter.recoverToken(request);
        return skillService.getYourSkills(jwt);
    }

    @GetMapping(path = "/details/{skillId}")
    public SkillDetailsDto getSkillDetails(@PathVariable Integer skillId) {
        return skillService.getSkillDetails(skillId);
    }

    @GetMapping(path = "/recommendedSkills")
    public List<SkillViewDto> getRecommendedSkills(HttpServletRequest request) {
        String jwt = securityFilter.recoverToken(request);
        return skillService.getRecommendedSkills(jwt);
    }
}
