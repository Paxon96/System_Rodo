package pl.p.lodz.system.rodo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.data.repository.query.QueryLookupStrategy.Key.CREATE;
import static org.springframework.data.repository.query.QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND;

@SpringBootApplication
@EnableJpaRepositories
@EnableScheduling
public class RodoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RodoApplication.class, args);
	}

}

