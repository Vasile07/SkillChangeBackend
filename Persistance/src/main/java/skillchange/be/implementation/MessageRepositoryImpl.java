package skillchange.be.implementation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import skillchange.be.MessageRepository;
import skillchange.be.ResultSetConvertor;
import skillchange.be.domain.Message;
import skillchange.be.dto.MessagePreviewDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MessageRepositoryImpl implements MessageRepository {
    @Value(value = "${spring.datasource.url}")
    private String connectionString;
    @Value(value = "${spring.datasource.username}")
    private String username;
    @Value(value = "${spring.datasource.password}")
    private String password;
    private final ResultSetConvertor resultSetConvertor;

    public MessageRepositoryImpl(ResultSetConvertor resultSetConvertor) {
        this.resultSetConvertor = resultSetConvertor;
    }

    @Override
    public void save(Message message) {
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("insert into Messages(message, sender_id, receiver_id, isRead) values (?,?,?,?)")
        ) {

            preparedStatement.setString(1, message.getMessage());
            preparedStatement.setInt(2, message.getSenderId());
            preparedStatement.setInt(3, message.getReceiverId());
            preparedStatement.setBoolean(4, message.getIsRead());

            int cols = preparedStatement.executeUpdate();

            if (cols == 0)
                throw new RuntimeException("Database error");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message saveMsg(Message message) {
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("insert into Messages(message, sender_id, receiver_id, isRead) values (?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)
        ) {

            preparedStatement.setString(1, message.getMessage());
            preparedStatement.setInt(2, message.getSenderId());
            preparedStatement.setInt(3, message.getReceiverId());
            preparedStatement.setBoolean(4, message.getIsRead());

            int cols = preparedStatement.executeUpdate();

            if (cols == 0)
                throw new RuntimeException("Database error");

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int insertedId = generatedKeys.getInt(1);
                    return findById(insertedId);
                } else {
                    throw new RuntimeException("Failed to retrieve inserted ID.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Message message) {

    }

    @Override
    public void delete(Integer integer) {

    }

    @Override
    public Message findById(Integer messageId) {
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("select * from Messages where message_id = ?")
        ) {

            preparedStatement.setInt(1, messageId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
                return resultSetConvertor.toMessage(resultSet);
            throw new RuntimeException("Message with id %d does not exists".formatted(messageId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Message> getAll() {
        return null;
    }

    @Override
    public List<Message> getChat(Integer firstUserId, Integer secondUserId) {
        List<Message> chat = new ArrayList<>();
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement("select * from Messages where sender_id = ? and receiver_id = ? or sender_id = ? and receiver_id = ?")
        ) {

            preparedStatement.setInt(1, firstUserId);
            preparedStatement.setInt(2, secondUserId);
            preparedStatement.setInt(3, secondUserId);
            preparedStatement.setInt(4, firstUserId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
                chat.add(resultSetConvertor.toMessage(resultSet));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return chat;
    }

    @Override
    public List<MessagePreviewDto> getChatPreviews(Integer userId) {
        List<MessagePreviewDto> chatPreviews = new ArrayList<>();
        try (
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                CallableStatement preparedStatement = connection.prepareCall("{call get_latest_messages_per_user(?)}");
        ) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                MessagePreviewDto messagePreviewDto = new MessagePreviewDto();
                messagePreviewDto.setMessageId(resultSet.getInt("message_id"));
                messagePreviewDto.setMessage(resultSet.getString("message"));
                messagePreviewDto.setUserName(resultSet.getString("user_name"));
                messagePreviewDto.setUserProfilePicture(resultSet.getString("profile_picture"));
                messagePreviewDto.setUserId(resultSet.getInt("user_id"));
                chatPreviews.add(messagePreviewDto);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return chatPreviews;
    }
}
