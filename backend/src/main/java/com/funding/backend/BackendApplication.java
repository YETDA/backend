package com.funding.backend;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
@Slf4j
public class BackendApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry ->
                {
                    System.setProperty(entry.getKey(), entry.getValue());
                }

        );
        SpringApplication.run(BackendApplication.class, args);
    }

}
