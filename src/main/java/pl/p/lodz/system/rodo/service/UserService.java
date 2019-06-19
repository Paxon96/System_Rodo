package pl.p.lodz.system.rodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.p.lodz.system.rodo.entity.User;
import pl.p.lodz.system.rodo.repo.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void changeStudentPassword(String newPassword, Authentication auth) {
        User user = userRepository.findFirstByLogin(auth.getName());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
