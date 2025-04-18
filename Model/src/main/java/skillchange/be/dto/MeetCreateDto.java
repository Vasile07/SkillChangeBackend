package skillchange.be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetCreateDto {
    private String day;
    private String startHour;
    private String endHour;
    private Integer skillId;
    private String color;
    private List<Integer> usersIds;
}
