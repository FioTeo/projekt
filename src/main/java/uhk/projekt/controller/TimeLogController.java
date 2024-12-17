package uhk.projekt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uhk.projekt.model.TimeLog;
import uhk.projekt.service.TimeLogService;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/timelogs")
public class TimeLogController {

    @Autowired
    private TimeLogService timeLogService;

    @PostMapping
    public ResponseEntity<?> createTimeLog(@Valid @RequestBody TimeLog timeLog, BindingResult result){
        if(result.hasErrors()){
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        try{
            TimeLog createdTimeLog = timeLogService.saveTimeLog(timeLog);
            return ResponseEntity.ok(createdTimeLog);
        } catch(RuntimeException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<TimeLog>> getAllTimeLogs(){
        List<TimeLog> timeLogs = timeLogService.getAllTimeLogs();
        return ResponseEntity.ok(timeLogs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeLog> getTimeLogById(@PathVariable Integer id){
        Optional<TimeLog> timeLog = timeLogService.getTimeLogById(id);
        return timeLog.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TimeLog>> getTimeLogsByUser(@PathVariable Integer userId){
        List<TimeLog> timeLogs = timeLogService.getAllTimeLogsByUser(userId);
        return ResponseEntity.ok(timeLogs);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TimeLog>> getTimeLogsByTask(@PathVariable Integer taskId){
        List<TimeLog> timeLogs = timeLogService.getAllTimeLogsByTask(taskId);
        return ResponseEntity.ok(timeLogs);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TimeLog>> getTimeLogsByProject(@PathVariable Integer projectId){
        List<TimeLog> timeLogs = timeLogService.getAllTimeLogsByProject(projectId);
        return ResponseEntity.ok(timeLogs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTimeLog(@PathVariable Integer id, @Valid @RequestBody TimeLog timeLog, BindingResult result){
        if(result.hasErrors()){
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        try{
            timeLog.setId(id);
            TimeLog updatedTimeLog = timeLogService.saveTimeLog(timeLog);
            return ResponseEntity.ok(updatedTimeLog);
        } catch(RuntimeException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeLog(@PathVariable Integer id){
        timeLogService.deleteTimeLogById(id);
        return ResponseEntity.noContent().build();
    }
}
