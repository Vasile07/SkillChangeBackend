package skillchange.be.implementation;

import org.springframework.stereotype.Service;
import skillchange.be.SkillRepository;
import skillchange.be.SkillService;
import skillchange.be.auth.TokenProvider;
import skillchange.be.dto.SkillViewDto;
import skillchange.be.mapper.SkillMapper;

import java.util.List;

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
}
