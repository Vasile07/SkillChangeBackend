package skillchange.be.implementation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import skillchange.be.MeetRepository;
import skillchange.be.domain.Meet;
import skillchange.be.domain.User;
import skillchange.be.dto.*;

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
    public Meet saveMeet(Meet meet) {
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
                    meet.setMeetId(meetId);
                    saveMeetUsers(meet.getUsers(), meetId);
                } else {
                    throw new RuntimeException("Failed to retrieve generated meet ID");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return meet;
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
    public List<MeetViewDto> getMeetsFromDate(Date date, Integer userId) {
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

    @Override
    public Integer getNumberOfTotalMeets(Integer userId) {
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("select count(distinct UM.meet_id) as 'meets_count' from UserMeets UM where UM.user_id = ?")
        ) {

            preparedStatement.setInt(1, userId);

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
    public List<TodayMeetDetail> getMeetsFromToday(Integer userId) {
        List<TodayMeetDetail> meets = new ArrayList<>();
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement(
                        """
                                SELECT
                                    S.name AS activity_name,
                                    CONCAT( 
                                            LEFT(CONVERT(varchar, M.start_hour, 108), 5),
                                            ' - ',
                                            LEFT(CONVERT(varchar, M.end_hour, 108), 5)
                                        ) AS hour,
                                    CASE
                                        WHEN CONVERT(TIME, GETDATE()) BETWEEN M.start_hour AND M.end_hour THEN 'IN_PROGRESS'
                                        WHEN CONVERT(TIME, GETDATE()) < M.start_hour THEN 'UPCOMING'
                                        ELSE 'DONE'
                                    END AS meet_state
                                FROM Meets M
                                JOIN UserMeets US ON US.meet_id = M.meet_id
                                JOIN Skills S ON S.skill_id = M.skill_id
                                WHERE CAST(M.day AS DATE) = CAST(GETDATE() AS DATE)
                                AND US.user_id = ?
                                ORDER BY M.start_hour
                                ;
                                """
                );

        ) {
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String activityName = resultSet.getString("activity_name");
                String hour = resultSet.getString("hour");
                String stateStr = resultSet.getString("meet_state");

                MeetState meetState = MeetState.valueOf(stateStr); // must match enum

                TodayMeetDetail detail = new TodayMeetDetail(meetState, activityName, hour);
                meets.add(detail);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return meets;
    }

    @Override
    public List<FutureMeetDetails> getNextMeets(Integer userId) {
        List<FutureMeetDetails> meetDetails = new ArrayList<>();
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement(
                        """
                                SELECT TOP 3
                                    S.name AS activity_name,
                                    S.picture AS activity_picture,
                                    CONVERT(varchar, M.start_hour, 108) AS start_hour,
                                    CONVERT(varchar, M.end_hour, 108) AS end_hour,
                                    FORMAT(M.day, 'yyyy-MM-dd') AS date,
                                    M.address,
                                    M.meet_id,
                                    M.color
                                FROM Meets M
                                JOIN UserMeets UM ON UM.meet_id = M.meet_id
                                JOIN Skills S ON S.skill_id = M.skill_id
                                WHERE UM.user_id = ?
                                  AND (M.day > CAST(GETDATE() AS DATE))
                                ORDER BY M.day, M.start_hour
                                """
                )
        ) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int meetId = resultSet.getInt("meet_id");

                String activityName = resultSet.getString("activity_name");
                String activityPicture = resultSet.getString("activity_picture");
                String startHour = resultSet.getString("start_hour");
                String endHour = resultSet.getString("end_hour");
                String date = resultSet.getString("date");
                String address = resultSet.getString("address");
                String color = resultSet.getString("color");

                List<UserMeetDto> users = getUsersForMeet(meetId, connection);

                FutureMeetDetails detail = new FutureMeetDetails(
                        activityName,
                        activityPicture,
                        startHour,
                        endHour,
                        date,
                        address,
                        users,
                        color
                );

                meetDetails.add(detail);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return meetDetails;
    }

    private List<UserMeetDto> getUsersForMeet(int meetId, Connection connection) throws SQLException {
        List<UserMeetDto> users = new ArrayList<>();

        String query = """
                SELECT U.firstname, U.lastname, U.profile_picture
                FROM Users U
                JOIN UserMeets UM ON UM.user_id = U.user_id
                WHERE UM.meet_id = ?
                """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, meetId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String firstname = rs.getString("firstname");
                String lastname = rs.getString("lastname");
                String profilePicture = rs.getString("profile_picture");

                users.add(new UserMeetDto(firstname, lastname, profilePicture));
            }
        }

        return users;
    }

}
