package fittoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FittoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(FittoringApplication.class, args);
    }

}
