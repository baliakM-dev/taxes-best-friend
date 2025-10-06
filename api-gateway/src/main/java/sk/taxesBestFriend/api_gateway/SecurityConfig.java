package sk.taxesBestFriend.api_gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorize -> authorize
                        // Explicitne povieme, že tieto cesty sú VEREJNÉ
                        .pathMatchers("/api/public/**",
                                "/api/register/**",
                                "/api/admin/**").permitAll()
                        // VŠETKY OSTATNÉ cesty vyžadujú prihlásenie
                        .anyExchange().authenticated()
                )
                // Pre tie chránené cesty zapneme OAuth2 login flow
                .oauth2Login(Customizer.withDefaults())
                .build();
    }
}