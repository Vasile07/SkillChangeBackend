package skillchange.be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageViewDto {
    private Integer messageId;
    private String message;
    private Integer senderId;
    private Integer receiverId;
}
