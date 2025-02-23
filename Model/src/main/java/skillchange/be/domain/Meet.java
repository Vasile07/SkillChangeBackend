package skillchange.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meet {
    private Integer meetId;
    private Date day;
    private Time hour;
    private Skill skill;
    private String color;
    private List<User> users;
}
