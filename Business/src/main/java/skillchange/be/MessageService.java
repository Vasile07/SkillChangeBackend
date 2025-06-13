package skillchange.be;

import skillchange.be.dto.MessageCreateDto;
import skillchange.be.dto.MessagePreviewDto;
import skillchange.be.dto.MessageViewDto;

import java.util.List;

public interface MessageService {
    MessageViewDto sendMessage(MessageCreateDto messageCreateDto, String jwt);

    List<MessageViewDto> getChat(String jwt, Integer otherUserId);

    List<MessagePreviewDto> getChatPreviews(String jwt);
}
