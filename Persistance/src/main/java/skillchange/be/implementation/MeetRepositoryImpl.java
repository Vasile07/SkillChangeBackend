package skillchange.be.implementation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import skillchange.be.MeetRepository;
import skillchange.be.domain.Meet;

import java.sql.*;
import java.util.List;

@Repository
public class MeetRepositoryImpl implements MeetRepository {

    @Value(value = "${spring.datasource.url}")
    private String connectionString;
    @Value(value = "${spring.datasource.username}")
    private String username;
    @Value(value = "${spring.datasource.password}")
    private String password;

    public MeetRepositoryImpl() {
    }

    @Override
    public void save(Meet meet) {
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("insert into Meets(day, hour, skill_id, color) values (?,?,?,?)")
        ) {

            preparedStatement.setDate(1, meet.getDay());
            preparedStatement.setTime(2, meet.getHour());
            preparedStatement.setInt(3, meet.getSkill().getSkillId());
            preparedStatement.setString(4, meet.getColor());

            int cols = preparedStatement.executeUpdate();

            if (cols == 0)
                throw new RuntimeException("Database error");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Meet meet) {

    }

    @Override
    public void delete(Integer integer) {

    }

    @Override
    public Meet findById(Integer integer) {
        return null;
    }

    @Override
    public List<Meet> getAll() {
        return null;
    }

    @Override
    public Integer numberOfMeetsInADayForAUser(Date date, Integer userId) {
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("select count(M.meet_id) as 'meets_count' from Meets M join UserMeets US on US.meet_id = M.meet_id where M.day = ? and US.user_id = ?")
        ) {

            preparedStatement.setDate(1, date);
            preparedStatement.setInt(2, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("meets_count");
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
