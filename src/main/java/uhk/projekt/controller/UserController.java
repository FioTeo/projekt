package uhk.projekt.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uhk.projekt.model.Project;
import uhk.projekt.model.User;
import uhk.projekt.service.UserService;

import java.util.ArrayList;

@Controller
@RequestMapping("/users/")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(@Qualifier("userService") UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(Model model) {
        ArrayList<User> users = userService.getAllUsers();

        model.addAttribute("users", users);

        return "user_index";
    }

    @GetMapping("/detail/{index}")
    public String detail(Model model, @PathVariable int index) {
        User user = userService.getUserById(index);
        if(user != null) {
            model.addAttribute("user", user);
            return "user_detail";
        }

        return "redirect:/users/";
    }

    @GetMapping("/delete/{index}")
    public String delete(Model model, @PathVariable int index) {
        userService.deleteUserById(index);
        return "redirect:/users/";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("user", new Project());
        model.addAttribute("edit", false);
        return "user_edit";
    }

    @GetMapping("/edit/{index}")
    public String edit(Model model, @PathVariable int index) {
        User user = userService.getUserById(index);
        if(user != null) {
            user.setId(index);
            model.addAttribute("user", user);
            model.addAttribute("edit", true);
            return "user_edit";
        }

        return "redirect:/users/";
    }

    @PostMapping("/save")
    public String save(@Valid User user, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("edit", true);
            return "user_edit";
        }
        userService.saveUser(user);
        return "redirect:/users/";
    }

}
