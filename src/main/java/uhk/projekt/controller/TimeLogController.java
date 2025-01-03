package uhk.projekt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uhk.projekt.model.TimeLog;
import uhk.projekt.service.TimeLogService;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/time_logs")
public class TimeLogController {

    @Autowired
    private TimeLogService timeLogService;

    /**
     * Display all time logs.
     */
    @GetMapping
    public String listTimeLogs(Model model) {
        List<TimeLog> timeLogs = timeLogService.getAllTimeLogs();
        model.addAttribute("timeLogs", timeLogs);
        return "time_log/time_logs"; // Corresponds to timelogs.html
    }

    /**
     * Display details of a specific time log.
     */
    @GetMapping("/detail/{id}")
    public String timeLogDetail(@PathVariable Integer id, Model model) {
        Optional<TimeLog> timeLog = timeLogService.getTimeLogById(id);
        if (timeLog.isPresent()) {
            model.addAttribute("timeLog", timeLog.get());
            return "time_log_detail"; // Corresponds to timelog_detail.html
        } else {
            return "redirect:/time_log?error=TimeLogNotFound";
        }
    }

    /**
     * Show the form to create a new time log.
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("timeLog", new TimeLog());
        return "time_log_edit"; // Corresponds to timelog_form.html
    }

    /**
     * Handle form submission for creating a new time log.
     */
    @PostMapping("/create")
    public String createTimeLog(@Valid @ModelAttribute("timeLog") TimeLog timeLog, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "time_log_edit";
        }
        try {
            timeLogService.saveTimeLog(timeLog);
            return "redirect:/time_logs?success=TimeLogCreated";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "time_log_edit";
        }
    }

    /**
     * Show the form to edit an existing time log.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Optional<TimeLog> timeLog = timeLogService.getTimeLogById(id);
        if (timeLog.isPresent()) {
            model.addAttribute("timeLog", timeLog.get());
            return "time_log/time_log_edit"; // Reuse the same form for editing
        } else {
            return "redirect:/time_logs?error=TimeLogNotFound";
        }
    }

    /**
     * Handle form submission for editing an existing time log.
     */
    @PostMapping("/edit/{id}")
    public String editTimeLog(@PathVariable Integer id, @Valid @ModelAttribute("timeLog") TimeLog timeLog, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "time_log_edit";
        }
        try {
            timeLog.setId(id); // Ensure the correct ID is set
            timeLogService.saveTimeLog(timeLog);
            return "redirect:/time_logs?success=TimeLogUpdated";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "time_log/time_log_edit";
        }
    }

    /**
     * Delete a time log.
     */
    @GetMapping("/delete/{id}")
    public String deleteTimeLog(@PathVariable Integer id) {
        try {
            timeLogService.deleteTimeLogById(id);
            return "redirect:/time_logs?success=TimeLogDeleted";
        } catch (RuntimeException ex) {
            return "redirect:/time_logs?error=" + ex.getMessage();
        }
    }
}