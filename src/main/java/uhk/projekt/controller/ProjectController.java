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
import java.time.LocalDate;
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
                return "redirect:/projects?error=AccessDenied";
            }

            List<User> allUsers = userService.getAllUsers();
            model.addAttribute("users", allUsers);
            model.addAttribute("project", project);
            model.addAttribute("task", new Task()); // Pro tvorbu nového úkolu
            return "project/project_detail";
        } else {
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
            return "project/project_edit";
        }
        try {
            String email = principal.getName();
            User currentUser = userService.findByEmail(email);
            project.setCreator(currentUser);
            projectService.saveProject(project);
            return "redirect:/projects?success=ProjectCreated";
        } catch (RuntimeException ex) {
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
                return "redirect:/projects?error=AccessDenied";
            }

            model.addAttribute("project", project);
            return "project/project_edit"; // Používáme stejný formulář pro editaci
        } else {
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
            return "project/project_edit";
        }
        try {
            String username = principal.getName();
            User currentUser = userService.findByEmail(username);
            Optional<Project> existingProjectOpt = projectService.getProjectById(id);
            if (existingProjectOpt.isPresent()) {
                Project existingProject = existingProjectOpt.get();

                if (!existingProject.getCreator().equals(currentUser)) {
                    return "redirect:/projects?error=AccessDenied";
                }

                existingProject.setName(project.getName());
                existingProject.setDescription(project.getDescription());
                existingProject.setBudget(project.getBudget());

                projectService.saveProject(existingProject);
                return "redirect:/projects?success=ProjectUpdated";
            } else {
                return "redirect:/projects?error=ProjectNotFound";
            }
        } catch (RuntimeException ex) {
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
                    return "redirect:/projects?error=AccessDenied";
                }
                projectService.deleteProjectById(id);
                return "redirect:/projects?success=ProjectDeleted";
            } else {
                return "redirect:/projects?error=ProjectNotFound";
            }
        } catch (RuntimeException ex) {
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
                return "redirect:/projects?error=AccessDenied";
            }
            Task task = new Task();
            task.setProject(project);
            model.addAttribute("task", task);
            model.addAttribute("users", userService.getAllUsers()); // Přidáno pro řešitele
            return "task/task_edit"; // Použijeme stejnou šablonu pro tvorbu a editaci úkolu
        } else {
            return "redirect:/projects?error=ProjectNotFound";
        }
    }

    @PostMapping("/detail/{projectId}/tasks/create")
    public String createTask(
            @PathVariable Integer projectId,
            @Valid @ModelAttribute("task") Task task,
            BindingResult result,
            @RequestParam(required = false) Integer solverId, // Accept solverId separately
            @RequestParam(required = false) String deadline, // Přijímá deadline z formuláře
            Model model,
            Principal principal) {

        // Fetch the project
        Optional<Project> projectOpt = projectService.getProjectById(projectId);
        if (projectOpt.isEmpty()) {
            return "redirect:/projects?error=ProjectNotFound";
        }
        Project project = projectOpt.get();

        // Fetch the current user
        String username = principal.getName();
        User currentUser = userService.findByEmail(username);
        if (!project.getCreator().equals(currentUser)) {
            return "redirect:/projects?error=AccessDenied";
        }

        // Handle validation errors
        if (result.hasErrors()) {
            model.addAttribute("project", project);
            model.addAttribute("users", userService.getAllUsers()); // Ensure users are available in the form
            return "task/task_edit";
        }

        // Fetch all users for the form in case of errors
        List<User> allUsers = userService.getAllUsers();

        try {
            // Parse and set deadline
            if (deadline != null && !deadline.isEmpty()) {
                LocalDate parsedDeadline = LocalDate.parse(deadline);
                task.setDeadline(parsedDeadline);
            } else {
                task.setDeadline(null); // No deadline assigned
            }

            // Assign solver if solverId is provided
            if (solverId != null) {
                Optional<User> solver = userService.getUserById(solverId);
                if (solver.isPresent()) {
                    task.setSolver(solver.get());
                } else {
                    // Solver not found, reject the value
                    result.rejectValue("solver", "NotFound", "Selected solver does not exist");
                    model.addAttribute("users", allUsers);
                    model.addAttribute("project", project);
                    return "task/task_edit";
                }
            } else {
                task.setSolver(null); // No solver assigned
            }

            // Set creator and project
            task.setCreator(currentUser);
            task.setProject(project);

            // Save the task
            taskService.saveTask(task);
            return "redirect:/projects/detail/" + projectId + "?success=TaskCreated";
        } catch (RuntimeException ex) {
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


        Optional<Project> projectOpt = projectService.getProjectById(projectId);
        Optional<Task> taskOpt = taskService.getTaskById(taskId);

        if (projectOpt.isEmpty() || taskOpt.isEmpty()) {
            return "redirect:/projects?error=ProjectOrTaskNotFound";
        }

        Project project = projectOpt.get();
        Task task = taskOpt.get();

        // Logování získaných objektů

        // Ověření, že úkol patří do projektu
        if (!task.getProject().getId().equals(projectId)) {
            return "redirect:/projects?error=InvalidTask";
        }

        // Ověření, že aktuální uživatel má přístup
        String username = principal.getName();
        User currentUser = userService.findByEmail(username);
        if (!project.getCreator().equals(currentUser)) {
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
            return "redirect:/projects?error=ProjectOrTaskNotFound";
        }

        Project project = projectOpt.get();
        Task task = taskOpt.get();

        if (!task.getProject().getId().equals(projectId)) {
            return "redirect:/projects?error=InvalidTask";
        }

        String username = principal.getName();
        User currentUser = userService.findByEmail(username);

        if (!project.getCreator().equals(currentUser)) {
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
            @RequestParam(required = false) Integer solverId, // Přijímá solverId z formuláře
            @RequestParam(required = false) String deadline, // Přijímá deadline z formuláře
            Model model,
            Principal principal) {

        Optional<Project> projectOpt = projectService.getProjectById(projectId);
        Optional<Task> taskOpt = taskService.getTaskById(taskId);

        if (projectOpt.isEmpty() || taskOpt.isEmpty()) {
            return "redirect:/projects?error=ProjectOrTaskNotFound";
        }

        Project project = projectOpt.get();
        Task existingTask = taskOpt.get();

        if (!existingTask.getProject().getId().equals(projectId)) {
            return "redirect:/projects?error=InvalidTask";
        }

        String username = principal.getName();
        User currentUser = userService.findByEmail(username);
        List<User> allUsers = userService.getAllUsers();

        if (!project.getCreator().equals(currentUser)) {
            return "redirect:/projects?error=AccessDenied";
        }

        // Zpracování validace
        if (result.hasErrors()) {
            model.addAttribute("users", allUsers);
            model.addAttribute("task", existingTask);
            model.addAttribute("project", project);
            return "task/task_edit";
        }

        try {
            // Parse and set deadline
            if (deadline != null && !deadline.isEmpty()) {
                LocalDate parsedDeadline = LocalDate.parse(deadline);
                existingTask.setDeadline(parsedDeadline);
            } else {
                existingTask.setDeadline(null); // No deadline assigned
            }

            // Aktualizace polí
            existingTask.setName(updatedTask.getName());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setPriority(updatedTask.getPriority());
            existingTask.setStatus(updatedTask.getStatus());

            // Aktualizace řešitele
            if (solverId != null) {
                Optional<User> solverOpt = userService.getUserById(solverId);
                if (solverOpt.isPresent()) {
                    existingTask.setSolver(solverOpt.get());
                } else {
                    // Resolver not found, přidejte chybu
                    result.rejectValue("solver", "NotFound", "Selected solver does not exist");
                    model.addAttribute("users", allUsers);
                    model.addAttribute("project", project);
                    return "task/task_edit";
                }
            } else {
                existingTask.setSolver(null); // Nenastaven řešitel
            }

            // Uložení úkolu
            taskService.saveTask(existingTask);
            return "redirect:/projects/detail/" + projectId + "/task/" + taskId + "?success=TaskUpdated";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("users", allUsers);
            model.addAttribute("task", existingTask);
            model.addAttribute("project", project);
            return "task/task_edit";
        }
    }

    @PostMapping("/detail/{projectId}/task/delete/{taskId}")
    public String deleteTask(
            @PathVariable Integer projectId,
            @PathVariable Integer taskId,
            Model model,
            Principal principal) {

        Optional<Project> projectOpt = projectService.getProjectById(projectId);
        Optional<Task> taskOpt = taskService.getTaskById(taskId);

        if (projectOpt.isEmpty() || taskOpt.isEmpty()) {
            return "redirect:/projects?error=ProjectOrTaskNotFound";
        }

        Project project = projectOpt.get();
        Task task = taskOpt.get();

        // Ověření, že úkol patří do projektu
        if (!task.getProject().getId().equals(projectId)) {
            return "redirect:/projects?error=InvalidTask";
        }

        // Získání autentizovaného uživatele
        String username = principal.getName();
        User currentUser = userService.findByEmail(username);

        // Ověření, že aktuální uživatel je tvůrcem projektu
        if (!project.getCreator().equals(currentUser)) {
            return "redirect:/projects?error=AccessDenied";
        }

        try {
            taskService.deleteTaskById(taskId);
            return "redirect:/projects/detail/" + projectId + "?success=TaskDeleted";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            // Znovu načteme detail úkolu s chybovou zprávou
            return projectDetail(projectId, model, principal);
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
            return "redirect:/projects?error=ProjectOrTaskNotFound";
        }

        Project project = projectOpt.get();
        Task task = taskOpt.get();

        // Ověření, že úkol patří do projektu
        if (!task.getProject().getId().equals(projectId)) {
            return "redirect:/projects?error=InvalidTask";
        }

        String username = principal.getName();
        User currentUser = userService.findByEmail(username);

        // Ověření, zda má uživatel přístup k projektu
        if (!project.getCreator().equals(currentUser)) {
            return "redirect:/projects?error=AccessDenied";
        }

        if (result.hasErrors()) {
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
            return "redirect:/projects/detail/" + projectId + "/task/" + taskId + "?success=TimeLogCreated";
        } catch (RuntimeException ex) {
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
            return "redirect:/projects?error=ProjectOrTaskNotFound";
        }

        Project project = projectOpt.get();
        Task task = taskOpt.get();

        if (!task.getProject().getId().equals(projectId)) {
            return "redirect:/projects?error=InvalidTask";
        }

        String username = principal.getName();
        User currentUser = userService.findByEmail(username);

        if (!project.getCreator().equals(currentUser)) {
            return "redirect:/projects?error=AccessDenied";
        }

        if (result.hasErrors()) {
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
            return "redirect:/projects/detail/" + projectId + "/task/" + taskId + "?success=MessageCreated";
        } catch (RuntimeException ex) {
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