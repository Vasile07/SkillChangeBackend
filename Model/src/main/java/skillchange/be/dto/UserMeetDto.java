package skillchange.be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMeetDto {
    private String firstname;
    private String lastname;
    private String profilePicture;
}
