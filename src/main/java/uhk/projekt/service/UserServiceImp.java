package uhk.projekt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uhk.projekt.model.Role;
import uhk.projekt.model.User;
import uhk.projekt.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired // Volitelná, pokud máte pouze jeden konstruktor
    public UserServiceImp(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

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

    /**
     * Uloží uživatele s přiřazenými rolemi.
     *
     * @param user      uživatel k uložení
     * @param roleNames seznam názvů rolí k přiřazení
     * @return uložený uživatel
     */
    @Transactional
    public User saveUser(User user, Set<String> roleNames) {
        Set<Role> roles = roleService.getRolesByNames(roleNames);
        user.setRoles(roles);
        // Šifrování hesla
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Aktualizuje uživatele na základě ID a rolí.
     *
     * @param id        ID uživatele
     * @param user      aktualizovaný uživatel
     * @param roleNames seznam názvů rolí k přiřazení
     * @return aktualizovaný uživatel
     */
    @Transactional
    public User updateUser(Integer id, User user, Set<String> roleNames) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isEmpty()) {
            throw new RuntimeException("Uživatel nebyl nalezen.");
        }
        User existingUser = existingUserOpt.get();
        existingUser.setName(user.getName());
        existingUser.setSurname(user.getSurname());
        existingUser.setEmail(user.getEmail());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        Set<Role> roles = roleService.getRolesByNames(roleNames);
        existingUser.setRoles(roles);
        return userRepository.save(existingUser);
    }

    /**
     * Smaže uživatele podle ID.
     *
     * @param id ID uživatele
     */
    public void deleteUserById(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Uživatel s ID " + id + " neexistuje.");
        }
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