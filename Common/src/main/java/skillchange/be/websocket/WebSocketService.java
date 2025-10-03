package skillchange.be.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import skillchange.be.dto.MeetingNotificationDto;
import skillchange.be.dto.MessageViewDto;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyUser(String jwt, MessageViewDto messageDto) {
        String destination = "/user/" + jwt + "/notifications";
        messagingTemplate.convertAndSend(destination, messageDto);
    }

    public void notifyUserOfMeeting(String jwt, MeetingNotificationDto meetingDto) {
        String destination = "/user/" + jwt + "/meeting-invites";
        messagingTemplate.convertAndSend(destination, meetingDto);
    }

}

