package skillchange.be.implementation;

import org.springframework.stereotype.Service;
import skillchange.be.MeetRepository;
import skillchange.be.MeetService;
import skillchange.be.auth.TokenProvider;

import java.sql.Date;
import java.time.LocalDate;

@Service
public class MeetServiceImpl implements MeetService {

    private final TokenProvider tokenProvider;
    private final MeetRepository meetRepository;

    public MeetServiceImpl(TokenProvider tokenProvider, MeetRepository meetRepository) {
        this.tokenProvider = tokenProvider;
        this.meetRepository = meetRepository;
    }

    @Override
    public Integer numberOfTodayMeetsAUser(String jwt) {
        Integer userId = Integer.valueOf(tokenProvider.validateToken(jwt));
        return meetRepository.numberOfMeetsInADayForAUser(Date.valueOf(LocalDate.now()), userId);
    }
}
