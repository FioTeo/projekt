package uhk.projekt.service;

import org.springframework.stereotype.Service;
import uhk.projekt.model.Message;
import uhk.projekt.model.Task;

import java.util.ArrayList;

@Service
public class TaskServiceImp implements TaskService {
    ArrayList<Task> tasks = new ArrayList<>();

    @Override
    public ArrayList<Task> getAllTasks() {
        return tasks;
    }

    @Override
    public ArrayList<Task> getAllTasksByProject(Task task) {
        return null;
    }

    @Override
    public ArrayList<Task> getAllTasksByUser(Task task) {
        return null;
    }

    @Override
    public Task getTaskById(int id) {
        if(id > -1 && id < tasks.size()) {
            return tasks.get(id);
        }
        return null;
    }

    @Override
    public void deleteTaskById(int id) {
        if(id > -1 && id < tasks.size()) {
            tasks.remove(id);
        }
    }

    @Override
    public void saveTask(Task task) {
        if(task.getId() > -1) {
            tasks.remove(task.getId());
        }
        tasks.add(task);
    }
}
