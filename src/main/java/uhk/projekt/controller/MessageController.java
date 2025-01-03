package uhk.projekt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uhk.projekt.model.Message;
import uhk.projekt.service.MessageService;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * Display all messages.
     */
    @GetMapping
    public String listMessages(Model model) {
        List<Message> messages = messageService.getAllMessages();
        model.addAttribute("messages", messages);
        return "message/message_index"; // Corresponds to messages.html
    }

    /**
     * Display details of a specific message.
     */
    @GetMapping("/detail/{id}")
    public String messageDetail(@PathVariable Integer id, Model model) {
        Optional<Message> message = messageService.getMessageById(id);
        if (message.isPresent()) {
            model.addAttribute("message", message.get());
            return "message/message_detail"; // Corresponds to message_detail.html
        } else {
            return "redirect:/messages?error=MessageNotFound";
        }
    }

    /**
     * Show the form to create a new message.
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("message", new Message());
        return "message/message_edit"; // Corresponds to message_edit.html
    }

    /**
     * Handle form submission for creating a new message.
     */
    @PostMapping("/create")
    public String createMessage(@Valid @ModelAttribute("message") Message message, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "message_edit";
        }
        try {
            messageService.saveMessage(message);
            return "redirect:/messages?success=MessageCreated";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "message/message_edit";
        }
    }

    /**
     * Show the form to edit an existing message.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Optional<Message> message = messageService.getMessageById(id);
        if (message.isPresent()) {
            model.addAttribute("message", message.get());
            return "message/message_edit"; // Reuse the same form for editing
        } else {
            return "redirect:/messages?error=MessageNotFound";
        }
    }

    /**
     * Handle form submission for editing an existing message.
     */
    @PostMapping("/edit/{id}")
    public String editMessage(@PathVariable Integer id, @Valid @ModelAttribute("message") Message message, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "message/message_edit";
        }
        try {
            message.setId(id); // Ensure the correct ID is set
            messageService.saveMessage(message);
            return "redirect:/messages?success=MessageUpdated";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "message/message_edit";
        }
    }

    /**
     * Delete a message.
     */
    @GetMapping("/delete/{id}")
    public String deleteMessage(@PathVariable Integer id) {
        try {
            messageService.deleteMessageById(id);
            return "redirect:/messages?success=MessageDeleted";
        } catch (RuntimeException ex) {
            return "redirect:/messages?error=" + ex.getMessage();
        }
    }
}