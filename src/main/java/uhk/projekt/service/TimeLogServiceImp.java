package uhk.projekt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uhk.projekt.model.TimeLog;
import uhk.projekt.repository.TimeLogRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TimeLogServiceImp implements TimeLogService {

    @Autowired
    private TimeLogRepository timeLogRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TimeLog> getAllTimeLogs() {
        return timeLogRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeLog> getAllTimeLogsByUser(Integer userId) {
        return timeLogRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeLog> getAllTimeLogsByTask(Integer taskId) {
        return timeLogRepository.findByTaskId(taskId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeLog> getAllTimeLogsByProject(Integer projectId) {
        return timeLogRepository.findByTask_ProjectId(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TimeLog> getTimeLogById(Integer id) {
        return timeLogRepository.findById(id);
    }

    @Override
    @Transactional
    public TimeLog saveTimeLog(TimeLog timeLog) {
        return timeLogRepository.save(timeLog);
    }

    @Override
    @Transactional
    public void deleteTimeLogById(Integer id) {
        timeLogRepository.deleteById(id);
    }
}
