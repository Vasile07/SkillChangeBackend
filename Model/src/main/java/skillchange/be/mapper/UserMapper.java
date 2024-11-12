package skillchange.be.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import skillchange.be.domain.User;
import skillchange.be.dto.UserCreateDto;
import skillchange.be.dto.UserViewDto;

@Mapper(componentModel = "spring", implementationPackage = "<PACKAGE_NAME>.generated", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User toEntity(UserCreateDto userCreateDto);

    UserViewDto toViewDto(User user);
}
