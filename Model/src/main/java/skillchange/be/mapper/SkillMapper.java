package skillchange.be.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import skillchange.be.domain.Skill;
import skillchange.be.dto.SkillDetailsDto;
import skillchange.be.dto.SkillViewDto;

@Mapper(componentModel = "spring", implementationPackage = "<PACKAGE_NAME>.generated", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {
    @Mapping(target = "type", ignore = true)
    SkillViewDto toViewDto(Skill skill);

    SkillDetailsDto toSkillDetails(Skill skill);
}
