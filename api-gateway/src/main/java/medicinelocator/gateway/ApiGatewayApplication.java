package medicinelocator.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;

// Exclude the auto-configuration bean that creates the fallback password
@SpringBootApplication(exclude = { ReactiveUserDetailsServiceAutoConfiguration.class })
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}