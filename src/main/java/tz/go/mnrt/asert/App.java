package tz.go.mnrt.asert;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@Slf4j
@EnableWebMvc
@EnableScheduling
@OpenAPIDefinition(info = @Info(title = "ASERT API Documentation", version = "1.0", description = "ASERT API Documentation"))
public class App {

    public static void main(String[] args) {
        log.info("********** ASERT is starting... **********");
        SpringApplication.run(App.class, args);
    }
}
