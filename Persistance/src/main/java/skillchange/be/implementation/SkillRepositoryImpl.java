package skillchange.be.implementation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import skillchange.be.SkillRepository;
import skillchange.be.domain.Skill;
import skillchange.be.domain.SkillType;
import skillchange.be.errors.EntityNotFound;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SkillRepositoryImpl implements SkillRepository {

    @Value(value = "${spring.datasource.url}")
    private String connectionString;
    @Value(value = "${spring.datasource.username}")
    private String username;
    @Value(value = "${spring.datasource.password}")
    private String password;

    public SkillRepositoryImpl() {
    }

    @Override
    public void save(Skill skill) {
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("insert into Skills(picutre, name, description, type_id) values (?,?, ?,?)")
        ) {

            preparedStatement.setString(1, skill.getPicture());
            preparedStatement.setString(2, skill.getName());
            preparedStatement.setString(3, skill.getDescription());
            preparedStatement.setInt(4, skill.getType().getTypeId());

            int cols = preparedStatement.executeUpdate();

            if (cols == 0)
                throw new RuntimeException("Database error");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Skill skill) {
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("update Skills set picutre = ?, name = ?, description = ?, type_id = ? where skill_id = ?")
        ) {

            preparedStatement.setString(1, skill.getPicture());
            preparedStatement.setString(2, skill.getName());
            preparedStatement.setString(3, skill.getDescription());
            preparedStatement.setInt(4, skill.getType().getTypeId());
            preparedStatement.setInt(5, skill.getSkillId());

            int cols = preparedStatement.executeUpdate();

            if (cols == 0)
                throw new RuntimeException("Database error");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Integer skillId) {
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("delete from Skills where skill_id = ?")
        ) {

            preparedStatement.setInt(1, skillId);

            int cols = preparedStatement.executeUpdate();

            if (cols == 0)
                throw new RuntimeException("Database error");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Skill findById(Integer skillId) {
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("select * from Skills where skill_id = ?")
        ) {

            preparedStatement.setInt(1, skillId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return toEntity(resultSet);
            } else {
                throw new EntityNotFound("Skill with id %d does not exists.".formatted(skillId));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Skill> getAll() {
        List<Skill> skills = new ArrayList<>();
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("select * from Skills")
        ) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
                skills.add(toEntity(resultSet));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return skills;
    }

    private Skill toEntity(ResultSet resultSet) throws SQLException {
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

    @Override
    public List<Skill> getSkillsForAUser(Integer userId) {
        List<Skill> skills = new ArrayList<>();
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("select S.* from Skills S join UserSkills US on S.skill_id = US.skill_id where US.user_id = ?")
        ) {

            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
                skills.add(toEntity(resultSet));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return skills;
    }

    @Override
    public List<Skill> getSkillsByType(String type) {
        List<Skill> skills = new ArrayList<>();
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("select S.* from Skills S join SkillTypes ST on ST.type_id = S.type_id where ST.type like ?")
        ) {
            preparedStatement.setString(1, type);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
                skills.add(toEntity(resultSet));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return skills;
    }
}
