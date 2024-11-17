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
import uhk.projekt.model.TimeLog;
import uhk.projekt.service.TaskService;
import uhk.projekt.service.TimeLogService;

import java.util.ArrayList;

@Controller
@RequestMapping("/time-logs/")
public class TimeLogController {
    private final TimeLogService timeLogService;

    @Autowired
    public TimeLogController(@Qualifier("timeLogService") TimeLogService timeLogService) {
        this.timeLogService = timeLogService;
    }

    @GetMapping("/")
    public String index(Model model) {
        ArrayList<TimeLog> timeLogs = timeLogService.getAllTimeLogs();
        model.addAttribute("TimeLog", new TimeLog());

        return "timeLog_index";
    }

    @GetMapping("/detail/{index}")
    public String detail(Model model, @PathVariable int index) {
        TimeLog TimeLog = timeLogService.getTimeLogById(index);
        if(TimeLog != null) {
            model.addAttribute("TimeLog", TimeLog);
            return "timeLog_detail";
        }

        return "redirect:/time-logs/";
    }

    @GetMapping("/delete/{index}")
    public String delete(Model model, @PathVariable int index) {
        timeLogService.deleteTimeLogById(index);
        return "redirect:/time-logs/";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("TimeLog", new TimeLog());
        model.addAttribute("edit", false);
        return "timeLog_edit";
    }

    @GetMapping("/edit/{index}")
    public String edit(Model model, @PathVariable int index) {
        TimeLog TimeLog = timeLogService.getTimeLogById(index);
        if(TimeLog != null) {
            TimeLog.setId(index);
            model.addAttribute("TimeLog", TimeLog);
            model.addAttribute("edit", true);
            return "timeLog_edit";
        }

        return "redirect:/time-logs/";
    }

    @PostMapping("/save")
    public String save(@Valid TimeLog TimeLog, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("edit", true);
            return "timeLog_edit";
        }
        timeLogService.saveTimeLog(TimeLog);
        return "redirect:/time-logs/";
    }
}
