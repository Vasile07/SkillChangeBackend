package skillchange.be.implementation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import skillchange.be.MeetRepository;
import skillchange.be.domain.Meet;
import skillchange.be.domain.User;
import skillchange.be.dto.MeetViewDto;

import java.sql.*;
import java.util.ArrayList;
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
                PreparedStatement preparedStatement = connection.prepareStatement("insert into Meets(day, start_hour,end_hour, skill_id, color) values (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setDate(1, meet.getDay());
            preparedStatement.setTime(2, meet.getStartHour());
            preparedStatement.setTime(3, meet.getEndHour());
            preparedStatement.setInt(4, meet.getSkill().getSkillId());
            preparedStatement.setString(5, meet.getColor());

            int cols = preparedStatement.executeUpdate();

            if (cols == 0)
                throw new RuntimeException("Database error");

            // meetId = getLastIdInsertedBefore
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int meetId = generatedKeys.getInt(1);
                    saveMeetUsers(meet.getUsers(), meetId);
                } else {
                    throw new RuntimeException("Failed to retrieve generated meet ID");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveMeetUsers(List<User> users, Integer meetId) {
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("insert into UserMeets(user_id, meet_id, status) values (?,?,?)")
        ) {
            for (User user : users) {
                preparedStatement.setInt(1, user.getUserId());
                preparedStatement.setInt(2, meetId);
                preparedStatement.setString(3, "Pending");
                preparedStatement.executeUpdate();
            }

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

    @Override
    public List<MeetViewDto> getMyMeetsFromDate(Date date, Integer userId) {
        List<MeetViewDto> meets = new ArrayList<>();
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement(
                        """
                                    SELECT\s
                                    S.name AS skill_name,\s
                                    CASE\s
                                    WHEN GETDATE() BETWEEN M.start_hour AND M.end_hour THEN 'In progress'\s
                                    WHEN GETDATE() < M.start_hour THEN 'Later'\s
                                    ELSE 'Done'\s
                                    END AS meet_progress\s
                                    FROM Meets M\s
                                    JOIN UserMeets US ON US.meet_id = M.meet_id\s
                                    JOIN Skills S ON S.skill_id = M.skill_id\s
                                    WHERE M.day = ?\s
                                    AND US.user_id = ?;\s
                                """
                )
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String skillName = resultSet.getString("skill_name");
                String meetProgress = resultSet.getString("meet_progress");
                MeetViewDto meetViewDto = new MeetViewDto(skillName, meetProgress);
                meets.add(meetViewDto);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return meets;
    }
}
