package skillchange.be.implementation;

import org.springframework.stereotype.Service;
import skillchange.be.SkillRepository;
import skillchange.be.SkillService;
import skillchange.be.auth.TokenProvider;
import skillchange.be.domain.Skill;
import skillchange.be.dto.SkillDetailsDto;
import skillchange.be.dto.SkillViewDto;
import skillchange.be.mapper.SkillMapper;
import skillchange.be.utils.Pair;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SkillServiceImpl implements SkillService {
    private final TokenProvider tokenProvider;
    private SkillRepository skillRepository;
    private SkillMapper skillMapper;


    public SkillServiceImpl(SkillRepository skillRepository, SkillMapper skillMapper, TokenProvider tokenProvider) {
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<SkillViewDto> getAllSkills() {
        return skillRepository.getAll()
                .stream()
                .map(s -> {
                    SkillViewDto skillViewDto = skillMapper.toViewDto(s);
                    skillViewDto.setType(s.getType().getType());
                    return skillViewDto;
                })
                .toList();
    }

    @Override
    public List<SkillViewDto> getYourSkills(String jwt) {
        Integer userId = Integer.valueOf(tokenProvider.validateToken(jwt));
        return skillRepository.getSkillsForAUser(userId).stream()
                .map(s -> {
                    SkillViewDto skillViewDto = skillMapper.toViewDto(s);
                    skillViewDto.setType(s.getType().getType());
                    return skillViewDto;
                })
                .toList();
    }

    @Override
    public List<SkillViewDto> getSkillsByType(String type) {
        return skillRepository.getSkillsByType(type).stream()
                .map(s -> {
                    SkillViewDto skillViewDto = skillMapper.toViewDto(s);
                    skillViewDto.setType(s.getType().getType());
                    return skillViewDto;
                })
                .toList();
    }

    @Override
    public SkillDetailsDto getSkillDetails(Integer skillId) {
        Skill skill = skillRepository.findById(skillId);
        SkillDetailsDto skillDetailsDto = skillMapper.toSkillDetails(skill);
        skillDetailsDto.setUsers(skillRepository.getUserLevelForSkill(skillId));
        return skillDetailsDto;
    }

    private Map<Integer, Pair<LocalTime, List<SkillViewDto>>> recommendedSkills = new HashMap<>();

    @Override
    public List<SkillViewDto> getRecommendedSkills(String jwt) {

        Integer userId = Integer.valueOf(tokenProvider.validateToken(jwt));
        Pair<LocalTime, List<SkillViewDto>> pair = recommendedSkills.get(userId);
        List<SkillViewDto> skills;
        if (pair == null || ChronoUnit.MINUTES.between(pair.getFirst(), LocalTime.now()) > 5) {
            do {
                skills = skillRepository.getRecommendedSkills(userId);
            } while (skills.size() != 3);
            recommendedSkills.put(userId, new Pair<>(LocalTime.now(), skills));
        } else
            skills = pair.getSecond();
        return skills;
    }
}
