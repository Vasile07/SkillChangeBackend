package skillchange.be;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import skillchange.be.domain.Meet;
import skillchange.be.domain.Skill;
import skillchange.be.domain.SkillType;
import skillchange.be.domain.User;
import skillchange.be.errors.EntityNotFound;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ResultSetConvertor {
    @Value(value = "${spring.datasource.url}")
    private String connectionString;
    @Value(value = "${spring.datasource.username}")
    private String username;
    @Value(value = "${spring.datasource.password}")
    private String password;

    public ResultSetConvertor() {
    }

    public User toUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUserId(resultSet.getInt("user_id"));
        user.setFirstname(resultSet.getString("firstname"));
        user.setLastname(resultSet.getString("lastname"));
        user.setCountry(resultSet.getString("country"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setResetToken(resultSet.getString("reset_token"));
        user.setProfilePicture(resultSet.getString("profile_picture"));
        user.setDescription(resultSet.getString("description"));
        return user;
    }

    public Meet toMeet(ResultSet resultSet) throws SQLException {
        Meet meet = new Meet();
        meet.setMeetId(resultSet.getInt("meet_id"));
        meet.setDay(resultSet.getDate("day"));
        meet.setStartHour(resultSet.getTime("start_hour"));
        meet.setEndHour(resultSet.getTime("end_hour"));
        meet.setColor(resultSet.getString("color"));
        meet.setSkill(getSkill(resultSet.getInt("skill_id")));
        meet.setUsers(getUsersFromMeet(resultSet.getInt("meet_id")));
        return meet;
    }

    public Skill toSkill(ResultSet resultSet) throws SQLException {
        Skill skill = new Skill();
        skill.setSkillId(resultSet.getInt("skill_id"));
        skill.setPicture(resultSet.getString("picture"));
        skill.setName(resultSet.getString("name"));
        skill.setDescription(resultSet.getString("description"));
        skill.setType(getTypeById(resultSet.getInt("type_id")));
        return skill;
    }


    private SkillType getTypeById(int typeId) {
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("select * from SkillTypes where type_id = ?")
        ) {

            preparedStatement.setInt(1, typeId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                SkillType skillType = new SkillType();
                skillType.setTypeId(resultSet.getInt("type_id"));
                skillType.setType(resultSet.getString("type"));
                return skillType;
            } else {
                throw new EntityNotFound("Skill type with id %d does not exists.".formatted(typeId));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private Skill getSkill(int skillId) {
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("select * from Skills where skill_id = ?")
        ) {

            preparedStatement.setInt(1, skillId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return toSkill(resultSet);
            } else {
                throw new EntityNotFound("Skill with id %d does not exists.".formatted(skillId));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<User> getUsersFromMeet(Integer meetId) {
        List<User> users = new ArrayList<>();
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("select U.* from UserMeets UM join Users U on UM.user_id = U.user_id where UM.meet_id = ?")
        ) {

            preparedStatement.setInt(1, meetId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                users.add(toUser(resultSet));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }
}
