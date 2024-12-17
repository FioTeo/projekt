package uhk.projekt.service;

import uhk.projekt.model.TimeLog;

import java.util.List;
import java.util.Optional;

public interface TimeLogService {
    List<TimeLog> getAllTimeLogs();
    List<TimeLog> getAllTimeLogsByUser(Integer userId);
    List<TimeLog> getAllTimeLogsByTask(Integer taskId);
    List<TimeLog> getAllTimeLogsByProject(Integer projectId);
    Optional<TimeLog> getTimeLogById(Integer id);
    TimeLog saveTimeLog(TimeLog timeLog);
    void deleteTimeLogById(Integer id);
}
