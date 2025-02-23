package skillchange.be;

import skillchange.be.domain.Meet;

import java.sql.Date;

public interface MeetRepository extends CrudRepository<Integer, Meet> {
    Integer numberOfMeetsInADayForAUser(Date date, Integer userId);
}
