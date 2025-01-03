package uhk.projekt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uhk.projekt.model.Message;
import uhk.projekt.repository.MessageRepository;
import uhk.projekt.repository.TaskRepository;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImp implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getAllMessagesByProject(Integer projectId) {
        return messageRepository.findByTask_Project_Id(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Message> getMessageById(Integer id) {
        return messageRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteMessageById(Integer id) {
        messageRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Message saveMessage(Message message) {
        if (message.getTask() != null && message.getTask().getId() == 0) {
            throw new IllegalArgumentException("Task must have a valid ID");
        }
        messageRepository.save(message);
        return message;
    }
}
