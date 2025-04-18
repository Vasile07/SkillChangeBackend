package skillchange.be;

import skillchange.be.dto.MeetCreateDto;
import skillchange.be.dto.MeetViewDto;

import java.sql.Date;
import java.util.List;

public interface MeetService {
    List<MeetViewDto> getMyMeetsFromDate(Date date, String jwt);

    void addMeet(MeetCreateDto meetCreateDto, String jwt);
    Integer numberOfTodayMeetsAUser(String jwt);
}
