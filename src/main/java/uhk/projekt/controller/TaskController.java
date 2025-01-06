package uhk.projekt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uhk.projekt.model.Task;
import uhk.projekt.model.User;
import uhk.projekt.service.TaskService;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import uhk.projekt.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listTasks(@RequestParam(name = "mine", required = false) Boolean mine, Model model, Principal principal) {
        try {
            List<Task> tasks;
            String currentUserEmail = principal.getName();
            User currentUser = userService.findByEmail(currentUserEmail);

            if (mine != null && mine) {
                tasks = taskService.getTasksAssignedTo(currentUser);
                model.addAttribute("mine", true);
            } else {
                tasks = taskService.getAllTasks();
                model.addAttribute("mine", false);
            }

            model.addAttribute("tasks", tasks);
            model.addAttribute("currentUser", currentUser);

            return "task/task_index";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Došlo k chybě při načítání úkolů.");
            return "error";
        }
    }
}