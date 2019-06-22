package pl.p.lodz.system.rodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.p.lodz.system.rodo.entity.Mark;
import pl.p.lodz.system.rodo.entity.Settings;
import pl.p.lodz.system.rodo.entity.User;
import pl.p.lodz.system.rodo.repo.MarkRepository;
import pl.p.lodz.system.rodo.repo.SettingsRepository;
import pl.p.lodz.system.rodo.repo.UserRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class ScheduledService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MarkRepository markRepository;
    @Autowired
    private SettingsRepository settingsRepository;

   @Scheduled(cron = "0 0 0 * * *")
//    @Scheduled(fixedRate = 5000) // for testing
    public void deleteMarksAfterSpecifiedDays(){
        List<User> userList = userRepository.findAll();
        List<Mark> marksToDelete = new ArrayList<>();
        Settings settings = settingsRepository.findAll().get(0);
        Calendar calendar = Calendar.getInstance();
        for (User user : userList) {
            List<Mark> userMarks = user.getMarks();
            for (Mark userMark : userMarks) {
                calendar.setTimeInMillis(userMark.getEvalDate().getTime());
                calendar.add(Calendar.DAY_OF_YEAR, settings.getDaysToDelete());
                Timestamp ts = new Timestamp(calendar.getTime().getTime());
                if(ts.before(new Timestamp(System.currentTimeMillis()))){
                    marksToDelete.add(userMark);
                }
            }
            if(marksToDelete.size() > 0) {
                user.getMarks().remove(marksToDelete);
                userRepository.save(user);

                for (Mark mark : marksToDelete) {
                    markRepository.deleteMark(mark.getId());
                }
            }
            marksToDelete.clear();
        }
    }
}
