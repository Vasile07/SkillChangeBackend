package skillchange.be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingNotificationDto {
    private Integer meetingId;
    private String title;
    private String organizer;
    private String date;
    private String time;
}