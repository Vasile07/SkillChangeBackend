package skillchange.be.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import skillchange.be.domain.Message;
import skillchange.be.dto.MessageCreateDto;
import skillchange.be.dto.MessageViewDto;

@Mapper(componentModel = "spring", implementationPackage = "<PACKAGE_NAME>.generated", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {
    Message toMessage(MessageCreateDto messageCreateDto);

    MessageViewDto toViewDto(Message message);
}
