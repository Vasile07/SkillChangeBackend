package skillchange.be.implementation;

import org.springframework.stereotype.Service;
import skillchange.be.MeetRepository;
import skillchange.be.MeetService;
import skillchange.be.SkillRepository;
import skillchange.be.UserRepository;
import skillchange.be.auth.TokenProvider;
import skillchange.be.domain.Meet;
import skillchange.be.domain.Skill;
import skillchange.be.domain.User;
import skillchange.be.dto.*;
import skillchange.be.mapper.MeetMapper;
import skillchange.be.websocket.WebSocketService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MeetServiceImpl implements MeetService {

    private final TokenProvider tokenProvider;
    private final MeetRepository meetRepository;
    private final MeetMapper meetMapper;
    private final WebSocketService webSocketService;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public MeetServiceImpl(TokenProvider tokenProvider, MeetRepository meetRepository, MeetMapper meetMapper, WebSocketService webSocketService, SkillRepository skillRepository, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.meetRepository = meetRepository;
        this.meetMapper = meetMapper;
        this.webSocketService = webSocketService;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
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
        List<Integer> userIds = new ArrayList<>(meetCreateDto.getUsersIds());
        userIds.add(userId);
        meet.setUsers(userIds.stream().map(uId -> new User(uId, null, null, null, null, null, null, null, null)).toList());
        meet.setSkill(new Skill(meetCreateDto.getSkillId(), null, null, null, null));
        Meet savedMeet = meetRepository.saveMeet(meet);
        User organizer = userRepository.findById(userId);
        MeetingNotificationDto meetingDto = new MeetingNotificationDto(savedMeet.getMeetId(), skillRepository.findById(meetCreateDto.getSkillId()).getName(), organizer.getFirstname() + " " + organizer.getLastname(), meetCreateDto.getDay(), meetCreateDto.getStartHour());
        notifyUsersForMeet(meetCreateDto.getUsersIds(), meetingDto);
    }

    private void notifyUsersForMeet(List<Integer> userIds, MeetingNotificationDto meetingDto) {
        userIds.stream()
                .map(uId -> {
                    User u = new User();
                    u.setUserId(uId);
                    return u;
                })
                .map(tokenProvider::generateAccessToken)
                .forEach(jwt -> webSocketService.notifyUserOfMeeting(jwt, meetingDto));
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
