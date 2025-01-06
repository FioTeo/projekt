package uhk.projekt.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "user/user_index";
    }

    @GetMapping("/detail/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String userDetail(@PathVariable Integer id, Model model) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "user/user_detail";
        } else {
            model.addAttribute("errorMessage", "Uživatel nebyl nalezen.");
            return "redirect:/users?error=UserNotFound";
        }
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "user/user_edit";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createUser(
            @Valid @ModelAttribute("user") User user,
            @RequestParam("roleNames") List<String> roles, // Změněno z "roles" na "roleNames"
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "user/user_edit";
        }
        try {
            Set<String> roleNames = new HashSet<>(roles);
            userService.saveUser(user, roleNames);
            return "redirect:/users?success=UserCreated";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "user/user_edit";
        }
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);
            model.addAttribute("allRoles", roleService.getAllRoles());
            Set<String> userRoles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());
            model.addAttribute("userRoles", userRoles);
            return "user/user_edit";
        } else {
            model.addAttribute("errorMessage", "Uživatel nebyl nalezen.");
            return "redirect:/users?error=UserNotFound";
        }
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editUser(
            @PathVariable Integer id,
            @Valid @ModelAttribute("user") User user,
            @RequestParam("roleNames") List<String> roles, // Změněno z "roles" na "roleNames"
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "user/user_edit";
        }
        try {
            Set<String> roleNames = new HashSet<>(roles);
            userService.saveUser(user, roleNames);
            return "redirect:/users?success=UserUpdated";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "user/user_edit";
        }
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable Integer id, Model model) {
        try {
            userService.deleteUserById(id);
            return "redirect:/users?success=UserDeleted";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "redirect:/users?error=" + ex.getMessage();
        }
    }
}