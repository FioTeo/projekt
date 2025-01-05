package uhk.projekt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uhk.projekt.model.Task;
import uhk.projekt.service.TaskService;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * Display all tasks.
     */
    @GetMapping
    public String listTasks(Model model) {
        List<Task> tasks = taskService.getAllTasks();
        model.addAttribute("tasks", tasks);
        return "task/task_index"; // Corresponds to tasks.html
    }

    /**
     * Display details of a specific task.
     */
    @GetMapping("/detail/{id}")
    public String taskDetail(@PathVariable Integer id, Model model) {
        Optional<Task> task = taskService.getTaskByIdWithProject(id);
        if (task.isPresent()) {
            model.addAttribute("task", task.get());
            return "task/task_detail"; // Corresponds to task_detail.html
        } else {
            return "redirect:/tasks?error=TaskNotFound";
        }
    }

    /**
     * Show the form to edit an existing task.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Optional<Task> taskOpt = taskService.getTaskByIdWithProject(id);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            model.addAttribute("task", task);
            return "task/task_edit"; // Reuse the same form for editing
        } else {
            return "redirect:/tasks?error=TaskNotFound";
        }
    }

    /**
     * Handle form submission for editing an existing task.
     */
    @PostMapping("/edit/{id}")
    public String editTask(@PathVariable Integer id,
                           @Valid @ModelAttribute("task") Task task,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            return "task/task_edit";
        }
        try {
            // Načtěte existující úkol spolu s projektem
            Optional<Task> existingTaskOpt = taskService.getTaskByIdWithProject(id);
            if (existingTaskOpt.isPresent()) {
                Task existingTask = existingTaskOpt.get();
                // Aktualizujte pouze relevantní pole
                existingTask.setName(task.getName());
                existingTask.setDescription(task.getDescription());
                existingTask.setPriority(task.getPriority());
                // Pokud chcete umožnit změnu projektu, musíte přidat logiku zde

                taskService.saveTask(existingTask);
                return "redirect:/tasks?success=TaskUpdated";
            } else {
                model.addAttribute("errorMessage", "Úkol nebyl nalezen.");
                return "redirect:/tasks?error=TaskNotFound";
            }
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "task/task_edit";
        }
    }

    /**
     * Delete a task.
     */
    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Integer id) {
        try {
            taskService.deleteTaskById(id);
            return "redirect:/tasks?success=TaskDeleted";
        } catch (RuntimeException ex) {
            return "redirect:/tasks?error=" + ex.getMessage();
        }
    }
}