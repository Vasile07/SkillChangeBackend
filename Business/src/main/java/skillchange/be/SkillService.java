package skillchange.be;

import skillchange.be.dto.SkillViewDto;

import java.util.List;

public interface SkillService {
    List<SkillViewDto> getAllSkills();

    List<SkillViewDto> getYourSkills(String jwt);

    List<SkillViewDto> getSkillsByType(String type);
}
