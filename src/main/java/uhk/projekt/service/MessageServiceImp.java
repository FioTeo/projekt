package uhk.projekt.service;

import org.springframework.stereotype.Service;
import uhk.projekt.model.Message;
import uhk.projekt.model.Project;

import java.util.ArrayList;

@Service
public class MessageServiceImp  implements MessageService {
    ArrayList<Message> messages = new ArrayList<>();

    @Override
    public ArrayList<Message> getAllMessages() {
        return messages;
    }

    @Override
    public ArrayList<Message> getAllMessagesByProject(Message message) {

        return null;
    }

    @Override
    public Message getMessageById(int id) {
        if(id > -1 && id < messages.size()) {
            return messages.get(id);
        }
        return null;
    }

    @Override
    public void deleteMessageById(int id) {
        if(id > -1 && id < messages.size()) {
            messages.remove(id);
        }
    }

    @Override
    public void saveMessage(Message message) {
        if(message.getId() > -1) {
            messages.remove(message.getId());
        }
        messages.add(message);
    }
}
