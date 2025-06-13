package skillchange.be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Integer messageId;
    private String message;
    private Integer senderId;
    private Integer receiverId;
    private Boolean isRead;
}
