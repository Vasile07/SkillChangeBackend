package skillchange.be.implementation;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
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

import java.io.IOException;
import java.io.InputStream;

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

    private String getResetCode() {
        return "1A2B3C";
    }

    @Override
    public void sendMail(String mail) {
        Context context = new Context();
        String resetCode = getResetCode();
        context.setVariable("code", resetCode);

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("mailsenderubb@gmail.com", "Mail Sender");
            helper.setTo(mail);
            helper.setSubject("Reset Your Password");

            String content = templateEngine.process("emailtemplate", context);
            helper.setText(content, true);

            // Add inline image
            InputStream imageStream = getClass().getResourceAsStream("/images/Passcode.png");
            if (imageStream != null) {
                ByteArrayDataSource dataSource = new ByteArrayDataSource(imageStream, "image/png");
                helper.addInline("authImage", dataSource);
            } else {
                throw new RuntimeException("Image not found in resources");
            }

            // Send the email
            mailSender.send(message);

        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
