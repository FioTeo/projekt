package uhk.projekt.service;

import org.springframework.stereotype.Service;
import uhk.projekt.model.Project;
import uhk.projekt.model.Task;
import uhk.projekt.model.TimeLog;
import uhk.projekt.model.User;

import java.util.ArrayList;

@Service
public class TimeLogServiceImp implements TimeLogService {
    ArrayList<TimeLog> timeLogs = new ArrayList<>();


    @Override
    public ArrayList<TimeLog> getAllTimeLogs() {
        return timeLogs;
    }

    @Override
    public ArrayList<TimeLog> getAllTimeLogsByUser(User user) {
        return null;
    }

    @Override
    public ArrayList<TimeLog> getAllTimeLogsByTask(Task task) {
        return null;
    }

    @Override
    public ArrayList<TimeLog> getAllTimeLogsProject(Project project) {
        return null;
    }

    @Override
    public TimeLog getTimeLogById(int id) {
        if(id > -1 && id < timeLogs.size()) {
            return timeLogs.get(id);
        }
        return null;
    }

    @Override
    public void deleteTimeLogById(int id) {
        if(id > -1 && id < timeLogs.size()) {
            timeLogs.remove(id);
        }
    }

    @Override
    public void saveTimeLog(TimeLog timeLog) {
        if(timeLog.getId() > -1) {
            timeLogs.remove(timeLog.getId());
        }
        timeLogs.add(timeLog);
    }
}
