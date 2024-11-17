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
import uhk.projekt.model.Message;
import uhk.projekt.service.MessageService;

import java.util.ArrayList;

@Controller
@RequestMapping("/messages/")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(@Qualifier("messageService") MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/")
    public String index(Model model) {
        ArrayList<Message> messages = messageService.getAllMessages();
        model.addAttribute("messages", messages);

        return "message_index";
    }

    @GetMapping("/detail/{index}")
    public String detail(Model model, @PathVariable int index) {
        Message Message = messageService.getMessageById(index);
        if(Message != null) {
            model.addAttribute("Message", Message);
            return "message_detail";
        }

        return "redirect:/messages/";
    }

    @GetMapping("/delete/{index}")
    public String delete(Model model, @PathVariable int index) {
        messageService.deleteMessageById(index);
        return "redirect:/messages/";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("Message", new Message());
        model.addAttribute("edit", false);
        return "message_edit";
    }

    @GetMapping("/edit/{index}")
    public String edit(Model model, @PathVariable int index) {
        Message Message = messageService.getMessageById(index);
        if(Message != null) {
            Message.setId(index);
            model.addAttribute("Message", Message);
            model.addAttribute("edit", true);
            return "message_edit";
        }

        return "redirect:/messages/";
    }

    @PostMapping("/save")
    public String save(@Valid Message Message, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("edit", true);
            return "message_edit";
        }
        messageService.saveMessage(Message);
        return "redirect:/messages/";
    }
}
