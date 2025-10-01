package fittoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class FittoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(FittoringApplication.class, args);
    }
}
