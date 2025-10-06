package sk.taxesBestFriend.user_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
// Kľúčová anotácia, ktorá zapína anotácie ako @PreAuthorize("hasRole('admin')")
@EnableMethodSecurity
public class SecurityConfig {

    // Zoznam všetkých verejne prístupných ciest
    private static final String[] PUBLIC_PATHS = {
            "/api/public/**",
            "/api/register/**", // Pridajme aj tento pre istotu
            "/api/admin/**",    // Pridajme aj tento pre istotu
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // 1. Vypneme CSRF ochranu, keďže používame stateless JWT autentifikáciu
                .csrf(csrf -> csrf.disable())

                // 2. Definujeme autorizačné pravidlá pre HTTP požiadavky
                .authorizeHttpRequests(authorize -> authorize
                        // Požiadavky na cesty v PUBLIC_PATHS sú povolené pre každého
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        // Všetky OSTATNÉ požiadavky musia byť autentifikované (musia mať platný token)
                        .anyRequest().authenticated()
                )

                // 3. Povieme Springu, aby sa správal ako OAuth2 Resource Server
                // a očakával a validoval JWT tokeny.
                .oauth2ResourceServer(oauth2 -> oauth2.jwt())

                // 4. Nastavíme session management na STATELESS.
                // Nevytvárame žiadne HTTP session na serveri, spoliehame sa výlučne na token.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .build();
    }
}