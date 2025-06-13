package skillchange.be;

import skillchange.be.domain.User;

import java.util.List;

public interface UserRepository extends CrudRepository<Integer, User> {
    User findByEmail(String email);

    List<User> getMyFriends(Integer userId);

    void follow(Integer followerUser, Integer followingUserId);

    List<User> getMyFollowers(Integer userId);
}
