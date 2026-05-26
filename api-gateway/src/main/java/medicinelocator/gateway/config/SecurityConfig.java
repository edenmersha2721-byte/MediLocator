package medicinelocator.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity; // 👈 MUST BE REACTIVE
import org.springframework.security.config.web.server.ServerHttpSecurity;                  // 👈 MUST BE SERVER
import org.springframework.security.web.server.SecurityWebFilterChain;                    // 👈 MUST BE SERVER

@Configuration
@EnableWebFluxSecurity // 👈 Configures WebFlux reactive security
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll() // Allow all gateway routing paths
                )
                .build();
    }
}