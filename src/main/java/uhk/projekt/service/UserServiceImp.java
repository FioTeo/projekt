package uhk.projekt.service;

import org.springframework.stereotype.Service;
import uhk.projekt.model.User;

import java.util.ArrayList;

@Service
public class UserServiceImp implements UserService {
    ArrayList<User> users = new ArrayList<>();

    @Override
    public ArrayList<User> getAllUsers() {
        return users;
    }

    @Override
    public User getUserById(int id) {
        if(id > -1 && id < users.size()) {
            return users.get(id);
        }
        return null;
    }

    @Override
    public void deleteUserById(int id) {
        if(id > -1 && id < users.size()) {
            users.remove(id);
        }
    }

    @Override
    public void saveUser(User user) {
        if(user.getId() > -1) {
            users.remove(user.getId());
        }
        users.add(user);
    }
}
