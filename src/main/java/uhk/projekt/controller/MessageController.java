package uhk.projekt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uhk.projekt.model.Message;
import uhk.projekt.model.Project;
import uhk.projekt.model.Task;
import uhk.projekt.model.User;
import uhk.projekt.service.MessageService;
import uhk.projekt.service.TaskService;
import uhk.projekt.service.ProjectService;
import uhk.projekt.service.UserService;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    /**
     * Zobrazí všechny zprávy.
     */
    @GetMapping
    public String listMessages(Model model) {
        List<Message> messages = messageService.getAllMessages();
        model.addAttribute("messages", messages);
        return "message/messages"; // Odpovídá messages.html
    }

    /**
     * Zobrazí detaily konkrétní zprávy.
     */
    @GetMapping("/detail/{id}")
    public String messageDetail(@PathVariable Integer id, Model model) {
        Optional<Message> messageOpt = messageService.getMessageById(id);
        if (messageOpt.isPresent()) {
            model.addAttribute("message", messageOpt.get());
            return "message/message_detail"; // Odpovídá message_detail.html
        } else {
            return "redirect:/messages?error=MessageNotFound";
        }
    }

    /**
     * Zobrazí formulář pro vytvoření nové zprávy.
     */
    @GetMapping("/create")
    public String showCreateForm(@RequestParam Integer taskId, Model model) {
        Message message = new Message();
        // Předpokládáme, že taskId je předán jako parametr
        model.addAttribute("message", message);
        model.addAttribute("taskId", taskId);
        return "message/message_form"; // Odpovídá message_form.html
    }

    /**
     * Zpracuje vytvoření nové zprávy.
     */
    @PostMapping("/create")
    public String createMessage(
            @RequestParam Integer taskId,
            @Valid @ModelAttribute("message") Message message,
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
            return "message/message_form";
        }

        try {
            message.setTask(task);
            message.setUser(currentUser);
            messageService.saveMessage(message);
            return "redirect:/projects/detail/" + task.getProject().getId() + "/task/" + taskId + "?success=MessageCreated";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("taskId", taskId);
            return "message/message_form";
        }
    }

    /**
     * Zobrazí formulář pro editaci existující zprávy.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, Principal principal) {
        Optional<Message> messageOpt = messageService.getMessageById(id);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            Task task = message.getTask();
            Project project = task.getProject();

            String username = principal.getName();
            User currentUser = userService.findByEmail(username);

            if (!project.getCreator().equals(currentUser)) {
                return "redirect:/projects?error=AccessDenied";
            }

            model.addAttribute("message", message);
            model.addAttribute("taskId", task.getId());
            return "message/message_form"; // Odpovídá message_form.html
        } else {
            return "redirect:/messages?error=MessageNotFound";
        }
    }

    /**
     * Zpracuje editaci existující zprávy.
     */
    @PostMapping("/edit/{id}")
    public String editMessage(
            @PathVariable Integer id,
            @RequestParam Integer taskId,
            @Valid @ModelAttribute("message") Message message,
            BindingResult result,
            Model model,
            Principal principal) {

        Optional<Message> messageOpt = messageService.getMessageById(id);
        if (messageOpt.isEmpty()) {
            return "redirect:/messages?error=MessageNotFound";
        }

        Message existingMessage = messageOpt.get();
        Task task = existingMessage.getTask();
        Project project = task.getProject();

        String username = principal.getName();
        User currentUser = userService.findByEmail(username);

        if (!project.getCreator().equals(currentUser)) {
            return "redirect:/projects?error=AccessDenied";
        }

        if (result.hasErrors()) {
            model.addAttribute("taskId", taskId);
            return "message/message_form";
        }

        try {
            existingMessage.setMessage(message.getMessage());
            messageService.saveMessage(existingMessage);
            return "redirect:/projects/detail/" + project.getId() + "/task/" + task.getId() + "?success=MessageUpdated";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("taskId", taskId);
            return "message/message_form";
        }
    }

    /**
     * Smaže zprávu.
     */
    @GetMapping("/delete/{id}")
    public String deleteMessage(@PathVariable Integer id, Principal principal) {
        Optional<Message> messageOpt = messageService.getMessageById(id);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            Task task = message.getTask();
            Project project = task.getProject();

            String username = principal.getName();
            User currentUser = userService.findByEmail(username);

            if (!project.getCreator().equals(currentUser)) {
                return "redirect:/projects?error=AccessDenied";
            }

            try {
                messageService.deleteMessageById(id);
                return "redirect:/projects/detail/" + project.getId() + "/task/" + task.getId() + "?success=MessageDeleted";
            } catch (RuntimeException ex) {
                return "redirect:/messages?error=" + ex.getMessage();
            }
        } else {
            return "redirect:/messages?error=MessageNotFound";
        }
    }
}