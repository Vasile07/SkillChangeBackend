package skillchange.be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSkillDto {
    private Integer userId;
    private String firstname;
    private String lastname;
    private String profilePicture;
    private Float skillLevel;
}
