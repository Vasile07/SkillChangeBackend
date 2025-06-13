package skillchange.be;

import skillchange.be.domain.Meet;
import skillchange.be.dto.FutureMeetDetails;
import skillchange.be.dto.MeetViewDto;
import skillchange.be.dto.TodayMeetDetail;

import java.sql.Date;
import java.util.List;

public interface MeetRepository extends CrudRepository<Integer, Meet> {
    Integer numberOfMeetsInADayForAUser(Date date, Integer userId);

    List<MeetViewDto> getMeetsFromDate(Date date, Integer userId);

    List<TodayMeetDetail> getMeetsFromToday(Integer userId);

    List<FutureMeetDetails> getNextMeets(Integer userId);

    Integer getNumberOfTotalMeets(Integer userId);
}
