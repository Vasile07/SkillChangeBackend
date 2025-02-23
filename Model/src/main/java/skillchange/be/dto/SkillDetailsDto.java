package skillchange.be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDetailsDto {
    private Integer skillId;
    private String name;
    private String description;
    private List<UserSkillDto> users;
}
