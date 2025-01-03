package uhk.projekt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uhk.projekt.model.User;
import uhk.projekt.service.UserService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Display the list of all users.
     */
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "user/user_index"; // Corresponds to users.html
    }

    /**
     * Display the details of a specific user.
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
     * Show the form to create a new user.
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "user/user_edit"; // Corresponds to user_edit.html
    }

    /**
     * Handle the submission of the create user form.
     */
    @PostMapping("/create")
    public String createUser(@ModelAttribute("user") User user, Model model) {
        try {
            userService.saveUser(user);
            return "redirect:/users?success=UserCreated";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "user/user_edit";
        }
    }

    /**
     * Show the form to edit an existing user.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "user/user_edit"; // Reuse the same form for editing
        } else {
            return "redirect:/users?error=UserNotFound";
        }
    }

    /**
     * Handle the submission of the edit user form.
     */
    @PostMapping("/edit/{id}")
    public String editUser(@PathVariable Integer id, @ModelAttribute("user") User user, Model model) {
        try {
            userService.saveUser(user);
            return "redirect:/users?success=UserUpdated";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "user/user_edit";
        }
    }

    /**
     * Handle the deletion of a user.
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