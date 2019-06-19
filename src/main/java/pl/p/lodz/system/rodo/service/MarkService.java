package pl.p.lodz.system.rodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.p.lodz.system.rodo.entity.User;
import pl.p.lodz.system.rodo.repo.MarkRepository;
import pl.p.lodz.system.rodo.repo.UserRepository;

@Service
public class MarkService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MarkRepository markRepository;

    public void deleteMark(int markId, Authentication auth) {
        User user = userRepository.findFirstByLogin(auth.getName());
        user.getMarks().remove(markRepository.findMarkById(markId));
        userRepository.save(user);
        markRepository.deleteMark(markId);
    }
}
