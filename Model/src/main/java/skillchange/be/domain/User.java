package skillchange.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer userId;
    private String firstname;
    private String lastname;
    private String country;
    private String email;
    private String password;
    private String resetToken;
    private String profilePicture;
    private String description;
}
