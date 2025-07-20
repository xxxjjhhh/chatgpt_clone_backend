package org.example.chatgpt_clone_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChatgptCloneBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatgptCloneBackendApplication.class, args);
    }

}
