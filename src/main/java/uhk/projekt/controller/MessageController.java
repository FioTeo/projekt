package uhk.projekt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uhk.projekt.model.Message;
import uhk.projekt.service.MessageService;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    public ResponseEntity<?> createMessage(@Valid @RequestBody Message message, BindingResult result){
        if(result.hasErrors()){
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        try{
            Message createdMessage = messageService.saveMessage(message);
            return ResponseEntity.ok(createdMessage);
        } catch(RuntimeException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages(){
        List<Message> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Integer id){
        Optional<Message> message = messageService.getMessageById(id);
        return message.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Message>> getMessagesByProject(@PathVariable Integer projectId){
        List<Message> messages = messageService.getAllMessagesByProject(projectId);
        return ResponseEntity.ok(messages);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMessage(@PathVariable Integer id, @Valid @RequestBody Message message, BindingResult result){
        if(result.hasErrors()){
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        try{
            message.setId(id);
            Message updatedMessage = messageService.saveMessage(message);
            return ResponseEntity.ok(updatedMessage);
        } catch(RuntimeException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Integer id){
        messageService.deleteMessageById(id);
        return ResponseEntity.noContent().build();
    }
}