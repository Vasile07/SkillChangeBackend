package skillchange.be.implementation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import skillchange.be.UserRepository;
import skillchange.be.domain.User;
import skillchange.be.errors.EntityNotFound;

import java.sql.*;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @Value(value = "${spring.datasource.url}")
    private String connectionString;
    @Value(value = "${spring.datasource.username}")
    private String username;
    @Value(value = "${spring.datasource.password}")
    private String password;

    public UserRepositoryImpl() {
    }

    @Override
    public void save(User user) {
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("insert into users(firstname, lastname, country, email, password) values (?,?,?,?,?)")
        ) {

            preparedStatement.setString(1, user.getFirstname());
            preparedStatement.setString(2, user.getLastname());
            preparedStatement.setString(3, user.getCountry());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setString(5, user.getPassword());

            int cols = preparedStatement.executeUpdate();

            if (cols == 0)
                throw new RuntimeException("Database error");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(Integer integer) {

    }

    @Override
    public void findById(Integer integer) {

    }

    @Override
    public List<User> getAll() {
        return null;
    }

    @Override
    public User findByEmail(String email) {
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("select * from Users where email like ?")
        ) {

            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return toEntity(resultSet);
            else
                throw new EntityNotFound("User with email %s does not exists".formatted(email));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User toEntity(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUserId(resultSet.getInt("user_id"));
        user.setFirstname(resultSet.getString("firstname"));
        user.setLastname(resultSet.getString("lastname"));
        user.setCountry(resultSet.getString("country"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        return user;
    }
}
