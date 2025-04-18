package skillchange.be.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import skillchange.be.domain.Meet;
import skillchange.be.dto.MeetCreateDto;

import java.sql.Date;
import java.sql.Time;

@Mapper(componentModel = "spring", implementationPackage = "<PACKAGE_NAME>.generated", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MeetMapper {
    @Mapping(target = "day", expression = "java(mapStringToDate(meetCreateDto.getDay()))")
    @Mapping(target = "startHour", expression = "java(mapStringToTime(meetCreateDto.getStartHour()))")
    @Mapping(target = "endHour", expression = "java(mapStringToTime(meetCreateDto.getEndHour()))")
    Meet toMeet(MeetCreateDto meetCreateDto);

    default Date mapStringToDate(String date) {
        return Date.valueOf(date);
    }

    default Time mapStringToTime(String time) {
        return Time.valueOf(time);
    }

}
