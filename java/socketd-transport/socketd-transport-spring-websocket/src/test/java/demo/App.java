package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author noear 2024/1/17 created
 */
@RestController
@SpringBootApplication
public class App {
    public static void main(String[] args){
        SpringApplication.run(App.class, args);
    }
}
