package skillchange.be.implementation;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import skillchange.be.UserRepository;
import skillchange.be.UserService;
import skillchange.be.auth.TokenProvider;
import skillchange.be.domain.User;
import skillchange.be.dto.JwtDto;
import skillchange.be.dto.LoginDto;
import skillchange.be.dto.UserCreateDto;
import skillchange.be.errors.BadCredentialsException;
import skillchange.be.errors.EntityNotFound;
import skillchange.be.mapper.UserMapper;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;

    //TODO: Define validator for User

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, JavaMailSender mailSender, TemplateEngine templateEngine, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public JwtDto login(LoginDto loginDto) {
        try {
            User user = userRepository.findByEmail(loginDto.getEmail());

            if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword()))
                throw new BadCredentialsException("Bad credentials");

            String jwtToken = tokenProvider.generateAccessToken(user);
            return new JwtDto(jwtToken);

        } catch (EntityNotFound entityNotFound) {
            throw new BadCredentialsException("Bad credentials");
        }
    }

    @Override
    public void save(UserCreateDto userCreateDto) {
        User user = userMapper.toEntity(userCreateDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}
