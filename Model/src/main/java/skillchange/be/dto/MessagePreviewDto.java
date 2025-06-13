package skillchange.be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessagePreviewDto {
    private String message;
    private String userName;
    private Integer userId;
    private String userProfilePicture;
    private Integer messageId;
}
