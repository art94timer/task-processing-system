package by.art.taskprocessingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TaskProcessingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskProcessingSystemApplication.class, args);
	}

}