package pl.p.lodz.system.rodo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.p.lodz.system.rodo.entity.Mark;
import pl.p.lodz.system.rodo.entity.User;

import java.util.List;

public interface MarkRepository extends JpaRepository<Mark, Long> {

    List<Mark> findFirstByUser(User user);

}
