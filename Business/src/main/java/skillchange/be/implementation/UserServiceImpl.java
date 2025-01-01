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
import skillchange.be.dto.*;
import skillchange.be.errors.BadCredentialsException;
import skillchange.be.errors.EntityNotFound;
import skillchange.be.errors.InvalidResetTokenException;
import skillchange.be.mapper.UserMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

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
    public void register(UserCreateDto userCreateDto) {
        User user = userMapper.toEntity(userCreateDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    private String getResetCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length());
            result.append(characters.charAt(index));
        }

        return result.toString();
    }

    @Override
    public void sendResetTokenMail(String mail) {

        Context context = new Context();
        String resetCode = getResetCode();

        User user = userRepository.findByEmail(mail);
        user.setResetToken(resetCode);
        userRepository.update(user);

        context.setVariable("code", resetCode);

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("mailsenderubb@gmail.com", "Mail Sender");
            helper.setTo(mail);
            helper.setSubject("Reset Your Password");

            String content = templateEngine.process("emailtemplate", context);
            helper.setText(content, true);

            InputStream imageStream = getClass().getResourceAsStream("/images/Passcode.png");
            if (imageStream != null) {
                ByteArrayDataSource dataSource = new ByteArrayDataSource(imageStream, "image/png");
                helper.addInline("authImage", dataSource);
            } else {
                throw new RuntimeException("Image not found in resources");
            }

            mailSender.send(message);

        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void validateResetToken(ResetTokenValidateDto resetTokenValidateDto) {
        User user = userRepository.findByEmail(resetTokenValidateDto.getEmail());
        if (!user.getResetToken().equals(resetTokenValidateDto.getResetToken()))
            throw new InvalidResetTokenException("Invalid reset token provided");
    }

    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        User user = userRepository.findByEmail(resetPasswordDto.getEmail());
        user.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
        userRepository.update(user);
    }

    @Override
    public UserViewDto getProfileData(String jwt) {
        Integer userId = Integer.valueOf(tokenProvider.validateToken(jwt));
        return userMapper.toViewDto(userRepository.findById(userId));
    }
}
