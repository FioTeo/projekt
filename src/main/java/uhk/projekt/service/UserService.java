package uhk.projekt.service;

import uhk.projekt.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Integer id);
    Optional<User> getUserByEmail(String email);
    User saveUser(User user);
    void deleteUserById(Integer id);
}