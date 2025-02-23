package skillchange.be;

import skillchange.be.domain.Skill;
import skillchange.be.dto.UserSkillDto;

import java.util.List;

public interface SkillRepository extends CrudRepository<Integer, Skill> {
    List<Skill> getSkillsForAUser(Integer userId);

    List<Skill> getSkillsByType(String type);

    List<UserSkillDto> getUserLevelForSkill(Integer skillId);
}
