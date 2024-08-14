package ai.softeer.caecae;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CaecaeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaecaeApplication.class, args);
    }

}
