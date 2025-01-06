package uhk.projekt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uhk.projekt.model.Role;
import uhk.projekt.model.User;
import uhk.projekt.service.RoleService;
import uhk.projekt.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    /**
     * Zobrazí seznam všech uživatelů.
     */
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "user/user_index"; // Corresponds to user_index.html
    }

    /**
     * Zobrazí detaily specifického uživatele.
     */
    @GetMapping("/detail/{id}")
    public String userDetail(@PathVariable Integer id, Model model) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "user/user_detail"; // Corresponds to user_detail.html
        } else {
            return "redirect:/users?error=UserNotFound";
        }
    }

    /**
     * Zobrazí formulář pro vytvoření nového uživatele.
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.getAllRoles()); // Přidáme všechny role do modelu
        return "user/user_edit"; // Corresponds to user_edit.html
    }

    /**
     * Zpracuje odeslání formuláře pro vytvoření nového uživatele.
     */
    @PostMapping("/create")
    public String createUser(
            @ModelAttribute("user") User user,
            @RequestParam("roles") List<String> roles,
            Model model) {
        try {
            Set<String> roleNames = new HashSet<>(roles);
            userService.saveUser(user, roleNames);
            return "redirect:/users?success=UserCreated";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("allRoles", roleService.getAllRoles()); // Zajistíme, že role jsou znovu načteny
            return "user/user_edit";
        }
    }

    /**
     * Zobrazí formulář pro editaci existujícího uživatele.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);
            model.addAttribute("allRoles", roleService.getAllRoles()); // Přidáme všechny role do modelu
            // Připravíme seznam rolí, které uživatel má
            Set<String> userRoles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());
            model.addAttribute("userRoles", userRoles);
            return "user/user_edit"; // Reuse the same form for editing
        } else {
            return "redirect:/users?error=UserNotFound";
        }
    }

    /**
     * Zpracuje odeslání formuláře pro editaci uživatele.
     */
    @PostMapping("/edit/{id}")
    public String editUser(
            @PathVariable Integer id,
            @ModelAttribute("user") User user,
            @RequestParam("roles") List<String> roles,
            Model model) {
        try {
            Set<String> roleNames = new HashSet<>(roles);
            userService.saveUser(user, roleNames);
            return "redirect:/users?success=UserUpdated";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("allRoles", roleService.getAllRoles()); // Zajistíme, že role jsou znovu načteny
            return "user/user_edit";
        }
    }

    /**
     * Zpracuje smazání uživatele.
     */
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUserById(id);
            return "redirect:/users?success=UserDeleted";
        } catch (RuntimeException ex) {
            return "redirect:/users?error=" + ex.getMessage();
        }
    }
}