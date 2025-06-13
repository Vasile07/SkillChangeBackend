package skillchange.be.Controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import skillchange.be.MessageService;
import skillchange.be.auth.SecurityFilter;
import skillchange.be.dto.MessageCreateDto;
import skillchange.be.dto.MessagePreviewDto;
import skillchange.be.dto.MessageViewDto;

import java.util.List;

@RestController
@RequestMapping("/messages")
@SecurityRequirement(name = "bearerAuth")
public class MessageController {
    private final MessageService messageService;
    private final SecurityFilter securityFilter;

    public MessageController(MessageService messageService, SecurityFilter securityFilter) {
        this.messageService = messageService;
        this.securityFilter = securityFilter;
    }

    @PostMapping("/send")
    public MessageViewDto sendMessage(@RequestBody MessageCreateDto messageCreateDto, HttpServletRequest request) {
        String jwt = securityFilter.recoverToken(request);
        return messageService.sendMessage(messageCreateDto, jwt);
    }

    @GetMapping("/chat/{userId}")
    public List<MessageViewDto> getChat(@PathVariable Integer userId, HttpServletRequest request) {
        String jwt = securityFilter.recoverToken(request);
        return messageService.getChat(jwt, userId);
    }

    @GetMapping("/chatPreviews")
    public List<MessagePreviewDto> getChatPreviews(HttpServletRequest request) {
        String jwt = securityFilter.recoverToken(request);
        return messageService.getChatPreviews(jwt);
    }

}
