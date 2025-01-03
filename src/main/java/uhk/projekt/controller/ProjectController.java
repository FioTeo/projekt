package uhk.projekt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uhk.projekt.model.Project;
import uhk.projekt.service.ProjectService;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    /**
     * Display all projects.
     */
    @GetMapping
    public String listProjects(Model model) {
        List<Project> projects = projectService.getAllProjects();
        model.addAttribute("projects", projects);
        return "project/project_index"; // Corresponds to projects.html
    }

    /**
     * Display details of a specific project.
     */
    @GetMapping("/detail/{id}")
    public String projectDetail(@PathVariable Integer id, Model model) {
        Optional<Project> project = projectService.getProjectById(id);
        if (project.isPresent()) {
            model.addAttribute("project", project.get());
            return "project/project_detail"; // Corresponds to project_detail.html
        } else {
            return "redirect:/projects?error=ProjectNotFound";
        }
    }

    /**
     * Show the form to create a new project.
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Project project = new Project();
        model.addAttribute("project", project);
        return "project/project_edit"; // Corresponds to project_edit.html
    }

    /**
     * Handle form submission for creating a new project.
     */
    @PostMapping("/create")
    public String createProject(@Valid @ModelAttribute("project") Project project, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "project_edit";
        }
        try {
            projectService.saveProject(project);
            return "redirect:/projects?success=ProjectCreated";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "project/project_edit";
        }
    }

    /**
     * Show the form to edit an existing project.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Optional<Project> project = projectService.getProjectById(id);
        if (project.isPresent()) {
            model.addAttribute("project", project.get());
            return "project/project_edit"; // Reuse the same form for editing
        } else {
            return "redirect:/projects?error=ProjectNotFound";
        }
    }

    /**
     * Handle form submission for editing an existing project.
     */
    @PostMapping("/edit/{id}")
    public String editProject(@PathVariable Integer id, @Valid @ModelAttribute("project") Project project, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "project/project_edit";
        }
        try {
            project.setId(id); // Ensure the correct ID is set
            projectService.saveProject(project);
            return "redirect:/projects?success=ProjectUpdated";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "project/project_edit";
        }
    }

    /**
     * Delete a project.
     */
    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable Integer id) {
        try {
            projectService.deleteProjectById(id);
            return "redirect:/projects?success=ProjectDeleted";
        } catch (RuntimeException ex) {
            return "redirect:/projects?error=" + ex.getMessage();
        }
    }
}