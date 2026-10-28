package fu.de191079.postservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class De191079PostServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(De191079PostServiceApplication.class, args);
    }
}

