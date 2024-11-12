package skillchange.be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserViewDto {
    private Integer userId;
    private String firstname;
    private String lastname;
    private String country;
    private String email;
    private String password;
}
