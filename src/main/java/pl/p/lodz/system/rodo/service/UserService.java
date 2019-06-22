package pl.p.lodz.system.rodo.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.p.lodz.system.rodo.entity.User;
import pl.p.lodz.system.rodo.repo.UserRepository;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private EmailService emailService;

    public void changeUserPassword(String newPassword, Authentication auth) {
        User user = userRepository.findFirstByLogin(auth.getName());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(newPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }

    public boolean findLoginInDatabase(String loginToFind) {
        return userRepository.findFirstByLogin(loginToFind) != null;
    }

    public void changeDaysSettings(String newDays, Authentication auth) {
        User user = userRepository.findFirstByLogin(auth.getName());
        if (user.getSettings().get(0).getDaysToDelete() != Short.parseShort(newDays)) {
            user.getSettings().get(0).setDaysToDelete(Short.parseShort(newDays));
        }
        userRepository.save(user);
    }

    public void sendEmailToNewUser(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String random = RandomStringUtils.random(8, true, true);
        userRepository.save(User.builder().login(user.getLogin()).password(passwordEncoder.encode(random)).permission("ROLE_USER").build());
        emailService.sendEmail(user.getLogin(), random);
    }
}
