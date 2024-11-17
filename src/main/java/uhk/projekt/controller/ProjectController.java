package uhk.projekt.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uhk.projekt.model.Project;
import uhk.projekt.service.ProjectService;

import java.util.ArrayList;

@Controller
@RequestMapping("/projects/")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(@Qualifier("projectService") ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/")
    public String index(Model model) {
        ArrayList<Project> projects = projectService.getAllProjects();
        model.addAttribute("projects", projects);

        return "project_index";
    }

    @GetMapping("/detail/{index}")
    public String detail(Model model, @PathVariable int index) {
        Project project = projectService.getProjectById(index);
        if(project != null) {
            model.addAttribute("project", project);
            return "project_detail";
        }

        return "redirect:/projects/";
    }

    @GetMapping("/delete/{index}")
    public String delete(Model model, @PathVariable int index) {
        projectService.deleteProjectById(index);
        return "redirect:/projects/";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("edit", false);
        return "project_edit";
    }

    @GetMapping("/edit/{index}")
    public String edit(Model model, @PathVariable int index) {
        Project project = projectService.getProjectById(index);
        if(project != null) {
            project.setId(index);
            model.addAttribute("project", project);
            model.addAttribute("edit", true);
            return "project_edit";
        }

        return "redirect:/projects/";
    }

    @PostMapping("/save")
    public String save(@Valid Project project, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("edit", true);
            return "project_edit";
        }
        projectService.saveProject(project);
        return "redirect:/projects/";
    }
}
