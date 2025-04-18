package skillchange.be;

import skillchange.be.domain.Meet;
import skillchange.be.dto.MeetViewDto;

import java.sql.Date;
import java.util.List;

public interface MeetRepository extends CrudRepository<Integer, Meet> {
    Integer numberOfMeetsInADayForAUser(Date date, Integer userId);

    List<MeetViewDto> getMyMeetsFromDate(Date date, Integer userId);
}
