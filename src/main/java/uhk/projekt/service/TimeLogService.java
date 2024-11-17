package uhk.projekt.service;

import uhk.projekt.model.Project;
import uhk.projekt.model.Task;
import uhk.projekt.model.TimeLog;
import uhk.projekt.model.User;

import java.util.ArrayList;

public interface TimeLogService {
    ArrayList<TimeLog> getAllTimeLogs();
    ArrayList<TimeLog> getAllTimeLogsByUser(User user);
    ArrayList<TimeLog> getAllTimeLogsByTask(Task task);
    ArrayList<TimeLog> getAllTimeLogsProject(Project project);
    TimeLog getTimeLogById(int id);
    void deleteTimeLogById(int id);
    void saveTimeLog(TimeLog timeLog);
}
