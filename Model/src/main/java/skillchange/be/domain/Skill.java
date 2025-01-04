package skillchange.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Skill {
    private Integer skillId;
    private String picture;
    private String name;
    private String description;
    private SkillType type;
}
