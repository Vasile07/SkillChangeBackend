package skillchange.be.Controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import skillchange.be.SkillService;
import skillchange.be.dto.SkillViewDto;

import java.util.List;


@RestController
@RequestMapping("/skills")
@SecurityRequirement(name = "bearerAuth")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
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
    public List<SkillViewDto> getYourSkills() {
        String jwt = "";
        return skillService.getYourSkills(jwt);
    }
}
