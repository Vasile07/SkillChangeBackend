package skillchange.be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FutureMeetDetails {
    private String activityName;
    private String activityPicture;
    private String startHour;
    private String endHour;
    private String date;
    private String address;
    private List<UserMeetDto> users;
    private String color;
}
