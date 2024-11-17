package uhk.projekt.service;

import org.springframework.stereotype.Service;
import uhk.projekt.model.User;

import java.util.ArrayList;

@Service
public interface UserService {
    ArrayList<User> getAllUsers();
    User getUserById(int id);
    void deleteUserById(int id);
    void saveUser(User user);
}
