package skillchange.be;

import skillchange.be.domain.Message;
import skillchange.be.dto.MessagePreviewDto;

import java.util.List;

public interface MessageRepository extends CrudRepository<Integer, Message> {
    public List<Message> getChat(Integer firstUserId, Integer secondUserId);

    public List<MessagePreviewDto> getChatPreviews(Integer userId);

    Message saveMsg(Message message);
}
