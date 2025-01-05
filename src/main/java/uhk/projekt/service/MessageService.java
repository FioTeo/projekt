package uhk.projekt.service;

import uhk.projekt.model.Message;

import java.util.List;
import java.util.Optional;

public interface MessageService {
    List<Message> getAllMessages();
    List<Message> getAllMessagesByProject(Integer projectId);
    Optional<Message> getMessageById(Integer id);
    void deleteMessageById(Integer id);
    void saveMessage(Message message);

    List<Message> getMessagesByTaskId(Integer taskId);
}
