package skillchange.be;

import skillchange.be.domain.User;

public interface UserRepository extends CrudRepository<Integer, User> {
    User findByEmail(String email);
}
