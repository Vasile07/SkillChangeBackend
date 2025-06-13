package skillchange.be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodayMeetDetail {
    private MeetState meetState;
    private String activityName;
    private String hour;
}
