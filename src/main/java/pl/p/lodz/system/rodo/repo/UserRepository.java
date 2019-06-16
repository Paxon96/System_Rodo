package pl.p.lodz.system.rodo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.p.lodz.system.rodo.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findFirstByLogin(String login);
}
