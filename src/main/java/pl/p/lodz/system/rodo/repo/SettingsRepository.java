package pl.p.lodz.system.rodo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.p.lodz.system.rodo.entity.Settings;

public interface SettingsRepository extends JpaRepository<Settings, Long> {

}
