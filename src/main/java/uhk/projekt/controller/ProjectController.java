package uhk.projekt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uhk.projekt.model.*;
import uhk.projekt.service.*;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private TimeLogService timeLogService;

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    /**
     * Zobrazí všechny projekty.
     */
    @GetMapping
    public String listProjects(Model model, Principal principal) {
        List<Project> projects = projectService.getAllProjects();
        model.addAttribute("projects", projects);
        return "project/project_index"; // Odpovídá projects.html
    }

    /**
     * Zobrazí detaily konkrétního projektu.
     */
    @GetMapping("/detail/{id}")
    public String projectDetail(@PathVariable Integer id, Model model, Principal principal) {
        Optional<Project> projectOpt = projectService.getProjectById(id);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();

            // Získání autentizovaného uživatele
            String username = principal.getName();
            User currentUser = userService.findByEmail(username);

            // Kontrola, zda je aktuální uživatel tvůrcem projektu
            if (!project.getCreator().equals(currentUser)) {
                logger.warn("User {} tried to access project {} without permission", username, id);
                return "redirect:/projects?error=AccessDenied";
            }

            List<User> allUsers = userService.getAllUsers();
            model.addAttribute("users", allUsers);
            model.addAttribute("project", project);
            model.addAttribute("task", new Task()); // Pro tvorbu nového úkolu
            return "project/project_detail";
        } else {
            logger.error("Project with ID {} not found", id);
            return "redirect:/projects?error=ProjectNotFound";
        }
    }

    /**
     * Zobrazí formulář pro vytvoření nového projektu.
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Project project = new Project();
        model.addAttribute("project", project);
        return "project/project_edit";
    }

    /**
     * Zpracuje vytvoření nového projektu.
     */
    @PostMapping("/create")
    public String createProject(
            @Valid @ModelAttribute("project") Project project,
            BindingResult result,
            Model model,
            Principal principal) {
        if (result.hasErrors()) {
            logger.warn("Validation errors while creating project: {}", result.getAllErrors());
            return "project/project_edit";
        }
        try {
            String email = principal.getName();
            User currentUser = userService.findByEmail(email);
            project.setCreator(currentUser);
            projectService.saveProject(project);
            logger.info("Project created successfully by user {}", currentUser.getEmail());
            return "redirect:/projects?success=ProjectCreated";
        } catch (RuntimeException ex) {
            logger.error("Error while creating project: {}", ex.getMessage());
            model.addAttribute("errorMessage", ex.getMessage());
            return "project/project_edit";
        }
    }

    /**
     * Zobrazí formulář pro editaci existujícího projektu.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, Principal principal) {
        Optional<Project> projectOpt = projectService.getProjectById(id);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();

            // Získání autentizovaného uživatele
            String username = principal.getName();
            User currentUser = userService.findByEmail(username);

            // Kontrola, zda je aktuální uživatel tvůrcem projektu
            if (!project.getCreator().equals(currentUser)) {
                logger.warn("User {} tried to edit project {} without permission", username, id);
                return "redirect:/projects?error=AccessDenied";
            }

            model.addAttribute("project", project);
            return "project/project_edit"; // Používáme stejný formulář pro editaci
        } else {
            logger.error("Project with ID {} not found for editing", id);
            return "redirect:/projects?error=ProjectNotFound";
        }
    }

    /**
     * Zpracuje editaci existujícího projektu.
     */
    @PostMapping("/edit/{id}")
    public String editProject(
            @PathVariable Integer id,
            @Valid @ModelAttribute("project") Project project,
            BindingResult result,
            Model model,
            Principal principal) {
        if (result.hasErrors()) {
            logger.warn("Validation errors while editing project {}: {}", id, result.getAllErrors());
            return "project/project_edit";
        }
        try {
            String username = principal.getName();
            User currentUser = userService.findByEmail(username);
            Optional<Project> existingProjectOpt = projectService.getProjectById(id);
            if (existingProjectOpt.isPresent()) {
                Project existingProject = existingProjectOpt.get();

                if (!existingProject.getCreator().equals(currentUser)) {
                    logger.warn("User {} tried to edit project {} without permission", username, id);
                    return "redirect:/projects?error=AccessDenied";
                }

                existingProject.setName(project.getName());
                existingProject.setDescription(project.getDescription());
                existingProject.setBudget(project.getBudget());

                projectService.saveProject(existingProject);
                logger.info("Project {} updated successfully by user {}", id, currentUser.getEmail());
                return "redirect:/projects?success=ProjectUpdated";
            } else {
                logger.error("Project with ID {} not found for editing", id);
                return "redirect:/projects?error=ProjectNotFound";
            }
        } catch (RuntimeException ex) {
            logger.error("Error while editing project {}: {}", id, ex.getMessage());
            model.addAttribute("errorMessage", ex.getMessage());
            return "project/project_edit";
        }
    }

    /**
     * Smaže projekt.
     */
    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable Integer id, Principal principal) {
        try {
            String username = principal.getName();
            User currentUser = userService.findByEmail(username);
            Optional<Project> projectOpt = projectService.getProjectById(id);
            if (projectOpt.isPresent()) {
                Project project = projectOpt.get();
                if (!project.getCreator().equals(currentUser)) {
                    logger.warn("User {} tried to delete project {} without permission", username, id);
                    return "redirect:/projects?error=AccessDenied";
                }
                projectService.deleteProjectById(id);
                logger.info("Project {} deleted successfully by user {}", id, currentUser.getEmail());
                return "redirect:/projects?success=ProjectDeleted";
            } else {
                logger.error("Project with ID {} not found for deletion", id);
                return "redirect:/projects?error=ProjectNotFound";
            }
        } catch (RuntimeException ex) {
            logger.error("Error while deleting project {}: {}", id, ex.getMessage());
            return "redirect:/projects?error=" + ex.getMessage();
        }
    }

    /**
     * Zobrazí formulář pro vytvoření nového úkolu v rámci projektu.
     */
    @GetMapping("/detail/{projectId}/tasks/create")
    public String showCreateTaskForm(@PathVariable Integer projectId, Model model, Principal principal) {
        Optional<Project> projectOpt = projectService.getProjectById(projectId);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            String username = principal.getName();
            User currentUser = userService.findByEmail(username);
            if (!project.getCreator().equals(currentUser)) {
                logger.warn("User {} tried to create task in project {} without permission", username, projectId);
                return "redirect:/projects?error=AccessDenied";
            }
            Task task = new Task();
            task.setProject(project);
            model.addAttribute("task", task);
            return "task/task_edit"; // Použijeme stejnou šablonu pro tvorbu a editaci úkolu
        } else {
            logger.error("Project with ID {} not found for creating task", projectId);
            return "redirect:/projects?error=ProjectNotFound";
        }
    }

    /**
     * Zpracuje vytvoření nového úkolu v rámci projektu.
     */
    @PostMapping("/detail/{projectId}/tasks/create")
    public String createTask(
            @PathVariable Integer projectId,
            @Valid @ModelAttribute("task") Task task,
            BindingResult result,
            Model model,
            Principal principal) {
        Optional<Project> projectOpt = projectService.getProjectById(projectId);
        if (projectOpt.isEmpty()) {
            return "redirect:/projects?error=ProjectNotFound";
        }
        Project project = projectOpt.get();

        String username = principal.getName();
        User currentUser = userService.findByEmail(username);
        if (!project.getCreator().equals(currentUser)) {
            return "redirect:/projects?error=AccessDenied";
        }

        if (result.hasErrors()) {
            model.addAttribute("project", project);
            return "task/task_edit";
        }

        List<User> allUsers = userService.getAllUsers();

        try {
            if (task.getSolver() != null && task.getSolver().getId() == null) {
                Optional<User> solver = userService.getUserById(task.getSolver().getId());
                task.setSolver(solver.orElse(null));
            }

            task.setCreator(currentUser);
            task.setProject(project);
            taskService.saveTask(task);
            logger.info("Task {} created successfully in project {} by user {}", task.getId(), projectId, currentUser.getEmail());
            return "redirect:/projects/detail/" + projectId + "?success=TaskCreated";
        } catch (RuntimeException ex) {
            logger.error("Error while creating task in project {}: {}", projectId, ex.getMessage());
            model.addAttribute("users", allUsers);
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("project", project);
            return "task/task_edit";
        }
    }

    /**
     * Zobrazí detail úkolu.
     */
    @GetMapping("/detail/{projectId}/task/{taskId}")
    public String taskDetail(
            @PathVariable Integer projectId,
            @PathVariable Integer taskId,
            Model model,
            Principal principal) {

        logger.info("Navigating to task detail: projectId={}, taskId={}", projectId, taskId);

        Optional<Project> projectOpt = projectService.getProjectById(projectId);
        Optional<Task> taskOpt = taskService.getTaskById(taskId);

        if (projectOpt.isEmpty() || taskOpt.isEmpty()) {
            logger.error("Project ID {} or Task ID {} not found", projectId, taskId);
            return "redirect:/projects?error=ProjectOrTaskNotFound";
        }

        Project project = projectOpt.get();
        Task task = taskOpt.get();

        // Logování získaných objektů
        logger.info("Retrieved Project: {}", project);
        logger.info("Retrieved Task: {}", task);

        // Ověření, že úkol patří do projektu
        if (!task.getProject().getId().equals(projectId)) {
            logger.warn("Task ID {} does not belong to Project ID {}", taskId, projectId);
            return "redirect:/projects?error=InvalidTask";
        }

        // Ověření, že aktuální uživatel má přístup
        String username = principal.getName();
        User currentUser = userService.findByEmail(username);
        logger.info("Current User: {}", currentUser.getEmail());
        if (!project.getCreator().equals(currentUser)) {
            logger.warn("User {} does not have access to Project ID {}", username, projectId);
            return "redirect:/projects?error=AccessDenied";
        }

        // Přidání potřebných atributů do modelu
        List<TimeLog> timeLogs = timeLogService.getTimeLogsByTaskId(taskId);
        List<Message> messages = messageService.getMessagesByTaskId(taskId);

        model.addAttribute("project", project);
        model.addAttribute("task", task);
        model.addAttribute("timeLogs", timeLogs);
        model.addAttribute("messages", messages);
        model.addAttribute("timeLog", new TimeLog()); // Pro přidání nového TimeLog
        model.addAttribute("message", new Message()); // Pro přidání nové zprávy

        // Logování stavu modelu
        logger.info("Model Attributes:");
        logger.info("Project: {}", model.getAttribute("project"));
        logger.info("Task: {}", model.getAttribute("task"));
        logger.info("TimeLogs count: {}", (timeLogs != null ? timeLogs.size() : "null"));
        logger.info("Messages count: {}", (messages != null ? messages.size() : "null"));
        logger.info("TimeLog object: {}", model.getAttribute("timeLog"));
        logger.info("Message object: {}", model.getAttribute("message"));

        return "task/task_detail"; // Šablona task_detail.html
    }

    /**
     * Zobrazí formulář pro editaci existujícího úkolu v rámci projektu.
     */
    @GetMapping("/detail/{projectId}/task/edit/{taskId}")
    public String showEditTaskForm(
            @PathVariable Integer projectId,
            @PathVariable Integer taskId,
            Model model,
            Principal principal) {

        Optional<Project> projectOpt = projectService.getProjectById(projectId);
        Optional<Task> taskOpt = taskService.getTaskById(taskId);

        if (projectOpt.isEmpty() || taskOpt.isEmpty()) {
            logger.error("Project ID {} or Task ID {} not found for editing task", projectId, taskId);
            return "redirect:/projects?error=ProjectOrTaskNotFound";
        }

        Project project = projectOpt.get();
        Task task = taskOpt.get();

        if (!task.getProject().getId().equals(projectId)) {
            logger.warn("Task ID {} does not belong to Project ID {}", taskId, projectId);
            return "redirect:/projects?error=InvalidTask";
        }

        String username = principal.getName();
        User currentUser = userService.findByEmail(username);

        if (!project.getCreator().equals(currentUser)) {
            logger.warn("User {} tried to edit task {} in project {} without permission", username, taskId, projectId);
            return "redirect:/projects?error=AccessDenied";
        }

        List<User> allUsers = userService.getAllUsers();

        model.addAttribute("users", allUsers);
        model.addAttribute("task", task);
        model.addAttribute("project", project);
        return "task/task_edit"; // Použijeme stejnou šablonu pro editaci
    }

    /**
     * Zpracuje editaci existujícího úkolu v rámci projektu.
     */
    @PostMapping("/detail/{projectId}/task/edit/{taskId}")
    public String editTask(
            @PathVariable Integer projectId,
            @PathVariable Integer taskId,
            @Valid @ModelAttribute("task") Task updatedTask,
            BindingResult result,
            Model model,
            Principal principal) {

        Optional<Project> projectOpt = projectService.getProjectById(projectId);
        Optional<Task> taskOpt = taskService.getTaskById(taskId);

        if (projectOpt.isEmpty() || taskOpt.isEmpty()) {
            logger.error("Project ID {} or Task ID {} not found for editing task", projectId, taskId);
            return "redirect:/projects?error=ProjectOrTaskNotFound";
        }

        Project project = projectOpt.get();
        Task existingTask = taskOpt.get();

        if (!existingTask.getProject().getId().equals(projectId)) {
            logger.warn("Task ID {} does not belong to Project ID {}", taskId, projectId);
            return "redirect:/projects?error=InvalidTask";
        }

        String username = principal.getName();
        User currentUser = userService.findByEmail(username);
        List<User> allUsers = userService.getAllUsers();

        if (!project.getCreator().equals(currentUser)) {
            logger.warn("User {} tried to edit task {} in project {} without permission", username, taskId, projectId);
            return "redirect:/projects?error=AccessDenied";
        }

        if (result.hasErrors()) {
            logger.warn("Validation errors while editing task {} in project {}: {}", taskId, projectId, result.getAllErrors());
            model.addAttribute("users", allUsers);
            model.addAttribute("task", existingTask);
            model.addAttribute("project", project);
            return "task/task_edit";
        }

        try {
            existingTask.setName(updatedTask.getName());
            existingTask.setDescription(updatedTask.getDescription());
            taskService.saveTask(existingTask);

            logger.info("Task ID {} updated successfully in project {} by user {}", taskId, projectId, currentUser.getEmail());
            return "redirect:/projects/detail/" + projectId + "?success=TaskUpdated";
        } catch (RuntimeException ex) {
            logger.error("Error while editing task {} in project {}: {}", taskId, projectId, ex.getMessage());
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("users", allUsers);
            model.addAttribute("task", existingTask);
            model.addAttribute("project", project);
            return "task/task_edit";
        }
    }

    /**
     * Zpracuje vytvoření nového TimeLogu pro úkol.
     */
    @PostMapping("/detail/{projectId}/task/{taskId}/timelogs/create")
    public String createTimeLog(
            @PathVariable Integer projectId,
            @PathVariable Integer taskId,
            @Valid @ModelAttribute("timeLog") TimeLog timeLog,
            BindingResult result,
            Model model,
            Principal principal) {

        Optional<Project> projectOpt = projectService.getProjectById(projectId);
        Optional<Task> taskOpt = taskService.getTaskById(taskId);

        if (projectOpt.isEmpty() || taskOpt.isEmpty()) {
            logger.error("Project ID {} or Task ID {} not found for creating TimeLog", projectId, taskId);
            return "redirect:/projects?error=ProjectOrTaskNotFound";
        }

        Project project = projectOpt.get();
        Task task = taskOpt.get();

        // Ověření, že úkol patří do projektu
        if (!task.getProject().getId().equals(projectId)) {
            logger.warn("Task ID {} does not belong to Project ID {}", taskId, projectId);
            return "redirect:/projects?error=InvalidTask";
        }

        String username = principal.getName();
        User currentUser = userService.findByEmail(username);

        // Ověření, zda má uživatel přístup k projektu
        if (!project.getCreator().equals(currentUser)) {
            logger.warn("User {} tried to create TimeLog in project {} without permission", username, projectId);
            return "redirect:/projects?error=AccessDenied";
        }

        if (result.hasErrors()) {
            logger.warn("Validation errors while creating TimeLog in task {}: {}", taskId, result.getAllErrors());
            // Přidání potřebných atributů do modelu
            List<TimeLog> timeLogs = timeLogService.getTimeLogsByTaskId(taskId);
            List<Message> messages = messageService.getMessagesByTaskId(taskId);
            model.addAttribute("project", project);
            model.addAttribute("task", task);
            model.addAttribute("timeLogs", timeLogs);
            model.addAttribute("messages", messages);
            // Přidání zpět `timeLog` aby byl dostupný pro formulář
            model.addAttribute("timeLog", timeLog);
            return "task/task_detail";
        }

        try {
            timeLog.setTask(task);
            timeLog.setUser(currentUser);
            timeLogService.saveTimeLog(timeLog);
            logger.info("TimeLog created successfully for Task ID {} by user {}", taskId, currentUser.getEmail());
            return "redirect:/projects/detail/" + projectId + "/task/" + taskId + "?success=TimeLogCreated";
        } catch (RuntimeException ex) {
            logger.error("Error while creating TimeLog for Task ID {}: {}", taskId, ex.getMessage());
            model.addAttribute("errorMessage", ex.getMessage());

            List<TimeLog> timeLogs = timeLogService.getTimeLogsByTaskId(taskId);
            List<Message> messages = messageService.getMessagesByTaskId(taskId);
            model.addAttribute("project", project);
            model.addAttribute("task", task);
            model.addAttribute("timeLogs", timeLogs);
            model.addAttribute("messages", messages);
            model.addAttribute("timeLog", timeLog);
            return "task/task_detail";
        }
    }

    @PostMapping("/detail/{projectId}/task/{taskId}/messages/create")
    public String createMessage(
            @PathVariable Integer projectId,
            @PathVariable Integer taskId,
            @Valid @ModelAttribute("message") Message message,
            BindingResult result,
            Model model,
            Principal principal) {

        Optional<Project> projectOpt = projectService.getProjectById(projectId);
        Optional<Task> taskOpt = taskService.getTaskById(taskId);

        if (projectOpt.isEmpty() || taskOpt.isEmpty()) {
            logger.error("Project ID {} or Task ID {} not found for creating Message", projectId, taskId);
            return "redirect:/projects?error=ProjectOrTaskNotFound";
        }

        Project project = projectOpt.get();
        Task task = taskOpt.get();

        if (!task.getProject().getId().equals(projectId)) {
            logger.warn("Task ID {} does not belong to Project ID {}", taskId, projectId);
            return "redirect:/projects?error=InvalidTask";
        }

        String username = principal.getName();
        User currentUser = userService.findByEmail(username);

        if (!project.getCreator().equals(currentUser)) {
            logger.warn("User {} tried to create Message in project {} without permission", username, projectId);
            return "redirect:/projects?error=AccessDenied";
        }

        if (result.hasErrors()) {
            logger.warn("Validation errors while creating Message in task {}: {}", taskId, result.getAllErrors());
            List<TimeLog> timeLogs = timeLogService.getTimeLogsByTaskId(taskId);
            List<Message> messages = messageService.getMessagesByTaskId(taskId);
            model.addAttribute("project", project);
            model.addAttribute("task", task);
            model.addAttribute("timeLogs", timeLogs);
            model.addAttribute("messages", messages);
            model.addAttribute("message", message);
            model.addAttribute("timeLog", new TimeLog());
            return "task/task_detail";
        }

        try {
            message.setTask(task);
            message.setUser(currentUser);
            messageService.saveMessage(message);
            logger.info("Message created successfully for Task ID {} by user {}", taskId, currentUser.getEmail());
            return "redirect:/projects/detail/" + projectId + "/task/" + taskId + "?success=MessageCreated";
        } catch (RuntimeException ex) {
            logger.error("Error while creating Message for Task ID {}: {}", taskId, ex.getMessage());
            model.addAttribute("errorMessage", ex.getMessage());

            List<TimeLog> timeLogs = timeLogService.getTimeLogsByTaskId(taskId);
            List<Message> messages = messageService.getMessagesByTaskId(taskId);
            model.addAttribute("project", project);
            model.addAttribute("task", task);
            model.addAttribute("timeLogs", timeLogs);
            model.addAttribute("messages", messages);
            model.addAttribute("message", message);
            model.addAttribute("timeLog", new TimeLog());
            return "task/task_detail";
        }
    }
}