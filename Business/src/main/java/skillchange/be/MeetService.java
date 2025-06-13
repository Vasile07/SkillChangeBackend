package skillchange.be;

import skillchange.be.dto.FutureMeetDetails;
import skillchange.be.dto.MeetCreateDto;
import skillchange.be.dto.MeetViewDto;
import skillchange.be.dto.TodayMeetDetail;

import java.sql.Date;
import java.util.List;

public interface MeetService {
    List<MeetViewDto> getMyMeetsFromDate(Date date, String jwt);

    void addMeet(MeetCreateDto meetCreateDto, String jwt);
    Integer numberOfTodayMeetsAUser(String jwt);

    Integer getTotalMeets(Integer userId);

    Integer getTotalMeets(String token);

    List<TodayMeetDetail> getTodayMeets(String jwt);

    List<FutureMeetDetails> getFutureMeets(String token);
}
