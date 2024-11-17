package uhk.projekt.service;

import uhk.projekt.model.Message;
import uhk.projekt.model.Project;
import uhk.projekt.model.Task;

import java.util.ArrayList;

public interface TaskService {
    ArrayList<Task> getAllTasks();
    ArrayList<Task> getAllTasksByProject(Task task);
    ArrayList<Task> getAllTasksByUser(Task task);
    Task getTaskById(int id);
    void deleteTaskById(int id);
    void saveTask(Task task);
}
