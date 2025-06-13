package skillchange.be.implementation;

import org.springframework.stereotype.Service;
import skillchange.be.MessageRepository;
import skillchange.be.MessageService;
import skillchange.be.auth.TokenProvider;
import skillchange.be.domain.Message;
import skillchange.be.domain.User;
import skillchange.be.dto.MessageCreateDto;
import skillchange.be.dto.MessagePreviewDto;
import skillchange.be.dto.MessageViewDto;
import skillchange.be.mapper.MessageMapper;
import skillchange.be.websocket.WebSocketService;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final TokenProvider tokenProvider;
    private final WebSocketService webSocketService;

    public MessageServiceImpl(MessageRepository messageRepository, MessageMapper messageMapper, TokenProvider tokenProvider, WebSocketService webSocketService) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.tokenProvider = tokenProvider;
        this.webSocketService = webSocketService;
    }

    @Override
    public MessageViewDto sendMessage(MessageCreateDto messageCreateDto, String jwt) {
        Message message = messageMapper.toMessage(messageCreateDto);
        Integer userId = Integer.valueOf(tokenProvider.validateToken(jwt));
        message.setSenderId(userId);
        message.setIsRead(false);
        Message messageAdded = messageRepository.saveMsg(message);
        MessageViewDto messageViewDto = messageMapper.toViewDto(messageAdded);
        if (messageAdded != null) {
            User other = new User();
            other.setUserId(messageCreateDto.getReceiverId());
            webSocketService.notifyUser(tokenProvider.generateAccessToken(other), messageViewDto);
        }

        return messageViewDto;
    }

    @Override
    public List<MessageViewDto> getChat(String jwt, Integer otherUserId) {
        Integer userId = Integer.valueOf(tokenProvider.validateToken(jwt));
        return messageRepository.getChat(userId, otherUserId).stream().map(messageMapper::toViewDto).toList();
    }

    @Override
    public List<MessagePreviewDto> getChatPreviews(String jwt) {
        Integer userId = Integer.valueOf(tokenProvider.validateToken(jwt));
        return messageRepository.getChatPreviews(userId);
    }
}
