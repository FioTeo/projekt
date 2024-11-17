package uhk.projekt.service;

import uhk.projekt.model.Message;

import java.util.ArrayList;

public interface MessageService {
    ArrayList<Message> getAllMessages();
    ArrayList<Message> getAllMessagesByProject(Message message);
    Message getMessageById(int id);
    void deleteMessageById(int id);
    void saveMessage(Message message);
}
