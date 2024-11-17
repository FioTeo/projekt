package uhk.projekt.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uhk.projekt.model.Task;
import uhk.projekt.service.TaskService;

import java.util.ArrayList;

@Controller
@RequestMapping("/tasks/")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(@Qualifier("taskService") TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/")
    public String index(Model model) {
        ArrayList<Task> tasks = taskService.getAllTasks();
        model.addAttribute("tasks", tasks);

        return "task_index";
    }

    @GetMapping("/detail/{index}")
    public String detail(Model model, @PathVariable int index) {
        Task Task = taskService.getTaskById(index);
        if(Task != null) {
            model.addAttribute("Task", Task);
            return "task_detail";
        }

        return "redirect:/tasks/";
    }

    @GetMapping("/delete/{index}")
    public String delete(Model model, @PathVariable int index) {
        taskService.deleteTaskById(index);
        return "redirect:/tasks/";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("Task", new Task());
        model.addAttribute("edit", false);
        return "task_edit";
    }

    @GetMapping("/edit/{index}")
    public String edit(Model model, @PathVariable int index) {
        Task Task = taskService.getTaskById(index);
        if(Task != null) {
            Task.setId(index);
            model.addAttribute("Task", Task);
            model.addAttribute("edit", true);
            return "task_edit";
        }

        return "redirect:/tasks/";
    }

    @PostMapping("/save")
    public String save(@Valid Task Task, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("edit", true);
            return "task_edit";
        }
        taskService.saveTask(Task);
        return "redirect:/tasks/";
    }
}
