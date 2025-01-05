package uhk.projekt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uhk.projekt.model.Project;
import uhk.projekt.model.Task;
import uhk.projekt.model.TimeLog;
import uhk.projekt.model.User;
import uhk.projekt.service.TimeLogService;
import uhk.projekt.service.TaskService;
import uhk.projekt.service.ProjectService;
import uhk.projekt.service.UserService;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/time_logs")
public class TimeLogController {

    @Autowired
    private TimeLogService timeLogService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    /**
     * Zobrazí všechny TimeLogy.
     */
    @GetMapping
    public String listTimeLogs(Model model) {
        List<TimeLog> timeLogs = timeLogService.getAllTimeLogs();
        model.addAttribute("timeLogs", timeLogs);
        return "time_log/time_logs"; // Odpovídá time_logs.html
    }

    /**
     * Zobrazí detaily konkrétního TimeLogu.
     */
    @GetMapping("/detail/{id}")
    public String timeLogDetail(@PathVariable Integer id, Model model) {
        Optional<TimeLog> timeLogOpt = timeLogService.getTimeLogById(id);
        if (timeLogOpt.isPresent()) {
            model.addAttribute("timeLog", timeLogOpt.get());
            return "time_log/time_log_detail"; // Odpovídá time_log_detail.html
        } else {
            return "redirect:/time_logs?error=TimeLogNotFound";
        }
    }

    /**
     * Zobrazí formulář pro vytvoření nového TimeLogu.
     */
    @GetMapping("/create")
    public String showCreateForm(@RequestParam Integer taskId, Model model) {
        TimeLog timeLog = new TimeLog();
        // Předpokládáme, že taskId je předán jako parametr
        model.addAttribute("timeLog", timeLog);
        model.addAttribute("taskId", taskId);
        return "time_log/time_log_form"; // Odpovídá time_log_form.html
    }

    /**
     * Zpracuje vytvoření nového TimeLogu.
     */
    @PostMapping("/create")
    public String createTimeLog(
            @RequestParam Integer taskId,
            @Valid @ModelAttribute("timeLog") TimeLog timeLog,
            BindingResult result,
            Model model,
            Principal principal) {

        Optional<Task> taskOpt = taskService.getTaskById(taskId);
        if (taskOpt.isEmpty()) {
            return "redirect:/projects?error=TaskNotFound";
        }

        Task task = taskOpt.get();

        String username = principal.getName();
        User currentUser = userService.findByEmail(username);

        if (!task.getProject().getCreator().equals(currentUser)) {
            return "redirect:/projects?error=AccessDenied";
        }

        if (result.hasErrors()) {
            model.addAttribute("taskId", taskId);
            return "time_log/time_log_form";
        }

        try {
            timeLog.setTask(task);
            timeLog.setUser(currentUser);
            timeLogService.saveTimeLog(timeLog);
            return "redirect:/projects/detail/" + task.getProject().getId() + "/task/" + taskId + "?success=TimeLogCreated";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("taskId", taskId);
            return "time_log/time_log_form";
        }
    }

    /**
     * Zobrazí formulář pro editaci existujícího TimeLogu.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, Principal principal) {
        Optional<TimeLog> timeLogOpt = timeLogService.getTimeLogById(id);
        if (timeLogOpt.isPresent()) {
            TimeLog timeLog = timeLogOpt.get();
            Task task = timeLog.getTask();
            Project project = task.getProject();

            String username = principal.getName();
            User currentUser = userService.findByEmail(username);

            if (!project.getCreator().equals(currentUser)) {
                return "redirect:/projects?error=AccessDenied";
            }

            model.addAttribute("timeLog", timeLog);
            model.addAttribute("taskId", task.getId());
            return "time_log/time_log_form"; // Odpovídá time_log_form.html
        } else {
            return "redirect:/time_logs?error=TimeLogNotFound";
        }
    }

    /**
     * Zpracuje editaci existujícího TimeLogu.
     */
    @PostMapping("/edit/{id}")
    public String editTimeLog(
            @PathVariable Integer id,
            @RequestParam Integer taskId,
            @Valid @ModelAttribute("timeLog") TimeLog timeLog,
            BindingResult result,
            Model model,
            Principal principal) {

        Optional<TimeLog> timeLogOpt = timeLogService.getTimeLogById(id);
        if (timeLogOpt.isEmpty()) {
            return "redirect:/time_logs?error=TimeLogNotFound";
        }

        TimeLog existingTimeLog = timeLogOpt.get();
        Task task = existingTimeLog.getTask();
        Project project = task.getProject();

        String username = principal.getName();
        User currentUser = userService.findByEmail(username);

        if (!project.getCreator().equals(currentUser)) {
            return "redirect:/projects?error=AccessDenied";
        }

        if (result.hasErrors()) {
            model.addAttribute("taskId", taskId);
            return "time_log/time_log_form";
        }

        try {
            existingTimeLog.setDescription(timeLog.getDescription());
            existingTimeLog.setTime(timeLog.getTime());
            timeLogService.saveTimeLog(existingTimeLog);
            return "redirect:/projects/detail/" + project.getId() + "/task/" + task.getId() + "?success=TimeLogUpdated";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("taskId", taskId);
            return "time_log/time_log_form";
        }
    }

    /**
     * Smaže TimeLog.
     */
    @GetMapping("/delete/{id}")
    public String deleteTimeLog(@PathVariable Integer id, Principal principal) {
        Optional<TimeLog> timeLogOpt = timeLogService.getTimeLogById(id);
        if (timeLogOpt.isPresent()) {
            TimeLog timeLog = timeLogOpt.get();
            Task task = timeLog.getTask();
            Project project = task.getProject();

            String username = principal.getName();
            User currentUser = userService.findByEmail(username);

            if (!project.getCreator().equals(currentUser)) {
                return "redirect:/projects?error=AccessDenied";
            }

            try {
                timeLogService.deleteTimeLogById(id);
                return "redirect:/projects/detail/" + project.getId() + "/task/" + task.getId() + "?success=TimeLogDeleted";
            } catch (RuntimeException ex) {
                return "redirect:/time_logs?error=" + ex.getMessage();
            }
        } else {
            return "redirect:/time_logs?error=TimeLogNotFound";
        }
    }
}