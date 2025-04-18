package skillchange.be.implementation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import skillchange.be.ResultSetConvertor;
import skillchange.be.UserRepository;
import skillchange.be.domain.User;
import skillchange.be.errors.EntityNotFound;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @Value(value = "${spring.datasource.url}")
    private String connectionString;
    @Value(value = "${spring.datasource.username}")
    private String username;
    @Value(value = "${spring.datasource.password}")
    private String password;

    private final ResultSetConvertor resultSetConvertor;

    public UserRepositoryImpl(ResultSetConvertor resultSetConvertor) {
        this.resultSetConvertor = resultSetConvertor;
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
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("update users set firstname = ?, lastname = ?, country = ?, email = ?, password = ?, reset_token = ?, profile_picture = ?, description = ? where user_id = ?")
        ) {

            preparedStatement.setString(1, user.getFirstname());
            preparedStatement.setString(2, user.getLastname());
            preparedStatement.setString(3, user.getCountry());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setString(5, user.getPassword());
            preparedStatement.setString(6, user.getResetToken());
            preparedStatement.setString(7, user.getProfilePicture());
            preparedStatement.setString(8, user.getDescription());
            preparedStatement.setInt(9, user.getUserId());

            int cols = preparedStatement.executeUpdate();

            if (cols == 0)
                throw new RuntimeException("Database error");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Integer userId) {
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("delete from users where user_id = ?")
        ) {

            preparedStatement.setInt(1, userId);

            int cols = preparedStatement.executeUpdate();

            if (cols == 0)
                throw new RuntimeException("Database error");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User findById(Integer userId) {
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("select * from Users where user_id = ?")
        ) {

            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return resultSetConvertor.toUser(resultSet);
            else
                throw new EntityNotFound("User with id %d does not exists".formatted(userId));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("select * from Users")
        ) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                users.add(resultSetConvertor.toUser(resultSet));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
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
                return resultSetConvertor.toUser(resultSet);
            else
                throw new EntityNotFound("User with email %s does not exists".formatted(email));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
