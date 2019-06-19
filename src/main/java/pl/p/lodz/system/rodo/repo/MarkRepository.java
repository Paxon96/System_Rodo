package pl.p.lodz.system.rodo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.p.lodz.system.rodo.entity.Mark;
import pl.p.lodz.system.rodo.entity.User;

import javax.transaction.Transactional;
import java.util.List;

public interface MarkRepository extends JpaRepository<Mark, Long> {

    List<Mark> findFirstByUserId(int userId);

    Mark findMarkById(int id);

    @Transactional
    @Modifying
    @Query("delete from Mark mark where mark.id=:x")
    void deleteMark(@Param("x") int markId);
}
