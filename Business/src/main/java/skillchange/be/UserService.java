package skillchange.be;

import skillchange.be.dto.*;

public interface UserService {
    JwtDto login(LoginDto loginDto);

    void register(UserCreateDto userCreateDto);

    void sendResetTokenMail(String mail);

    void validateResetToken(ResetTokenValidateDto resetTokenValidateDto);

    void resetPassword(ResetPasswordDto resetPasswordDto);

    UserViewDto getProfileData(String jwt);

    UserViewDto getProfileData(Integer userId);
}
