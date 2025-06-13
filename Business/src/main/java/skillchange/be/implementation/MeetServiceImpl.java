package skillchange.be.implementation;

import org.springframework.stereotype.Service;
import skillchange.be.MeetRepository;
import skillchange.be.MeetService;
import skillchange.be.auth.TokenProvider;
import skillchange.be.domain.Meet;
import skillchange.be.domain.Skill;
import skillchange.be.domain.User;
import skillchange.be.dto.FutureMeetDetails;
import skillchange.be.dto.MeetCreateDto;
import skillchange.be.dto.MeetViewDto;
import skillchange.be.dto.TodayMeetDetail;
import skillchange.be.mapper.MeetMapper;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class MeetServiceImpl implements MeetService {

    private final TokenProvider tokenProvider;
    private final MeetRepository meetRepository;
    private final MeetMapper meetMapper;

    public MeetServiceImpl(TokenProvider tokenProvider, MeetRepository meetRepository, MeetMapper meetMapper) {
        this.tokenProvider = tokenProvider;
        this.meetRepository = meetRepository;
        this.meetMapper = meetMapper;
    }

    @Override
    public List<MeetViewDto> getMyMeetsFromDate(Date date, String jwt) {
        Integer userId = Integer.valueOf(tokenProvider.validateToken(jwt));
        return meetRepository.getMeetsFromDate(date, userId);
    }

    @Override
    public void addMeet(MeetCreateDto meetCreateDto, String jwt) {
        Integer userId = Integer.valueOf(tokenProvider.validateToken(jwt));
        Meet meet = meetMapper.toMeet(meetCreateDto);
        List<Integer> userIds = meetCreateDto.getUsersIds();
        userIds.add(userId);
        meet.setUsers(userIds.stream().map(uId -> new User(uId, null, null, null, null, null, null, null, null)).toList());
        meet.setSkill(new Skill(meetCreateDto.getSkillId(), null, null, null, null));
        meetRepository.save(meet);
    }

    @Override
    public Integer numberOfTodayMeetsAUser(String jwt) {
        Integer userId = Integer.valueOf(tokenProvider.validateToken(jwt));
        return meetRepository.numberOfMeetsInADayForAUser(Date.valueOf(LocalDate.now()), userId);
    }

    @Override
    public Integer getTotalMeets(Integer userId) {
        return meetRepository.getNumberOfTotalMeets(userId);
    }

    @Override
    public Integer getTotalMeets(String token) {
        Integer userId = Integer.valueOf(tokenProvider.validateToken(token));
        return getTotalMeets(userId);
    }

    @Override
    public List<TodayMeetDetail> getTodayMeets(String jwt) {
        Integer userId = Integer.valueOf(tokenProvider.validateToken(jwt));
        return meetRepository.getMeetsFromToday(userId);
    }

    @Override
    public List<FutureMeetDetails> getFutureMeets(String token) {
        Integer userId = Integer.valueOf(tokenProvider.validateToken(token));
        return meetRepository.getNextMeets(userId);
    }
}
