package skillchange.be;

import skillchange.be.dto.*;

import java.util.List;

public interface UserService {
    JwtDto login(LoginDto loginDto);

    void register(UserCreateDto userCreateDto);

    void sendResetTokenMail(String mail);

    void validateResetToken(ResetTokenValidateDto resetTokenValidateDto);

    void resetPassword(ResetPasswordDto resetPasswordDto);

    UserViewDto getProfileData(String jwt);

    UserViewDto getProfileData(Integer userId);

    List<UserViewDto> getMyFriends(String jwt);

    void follow(String jwt, Integer followingUserId);

    List<UserViewDto> getFollowers(String token);

    List<UserViewDto> getFollowers(Integer userId);
}
