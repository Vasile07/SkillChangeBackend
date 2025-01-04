package skillchange.be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillViewDto {
    private Integer skillId;
    private String picture;
    private String name;
    private String description;
    private String type;
}
