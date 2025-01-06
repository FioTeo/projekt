package uhk.projekt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uhk.projekt.model.Role;
import uhk.projekt.model.User;
import uhk.projekt.repository.UserRepository;
import uhk.projekt.security.SecurityConfig;
import uhk.projekt.security.SharedConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SharedConfig sharedConfig;

    @Autowired
    private RoleService roleService;

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User saveUser(User user, Set<String> roleNames) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(user.getPassword());
        }

        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            Optional<Role> roleOpt = roleService.getRoleByName(roleName);
            roleOpt.ifPresent(roles::add);
        }
        user.setRoles(roles);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUserById(Integer id) {
        userRepository.deleteById(id);
    }

    /**
     * Find a user by their email.
     *
     * @param email The user's email.
     * @return The User entity.
     * @throws RuntimeException if the user is not found.
     */
    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
}
