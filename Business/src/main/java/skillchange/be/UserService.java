package skillchange.be;

import skillchange.be.dto.JwtDto;
import skillchange.be.dto.LoginDto;
import skillchange.be.dto.UserCreateDto;

public interface UserService {
    JwtDto login(LoginDto loginDto);

    void save(UserCreateDto userCreateDto);

    void sendMail(String mail);
}
