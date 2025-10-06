package sk.taxesBestFriend.user_service;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
    "/api/register/**",
    "/api/admin/**",
    "/v3/api-docs/**",
    "/swagger-ui/**",
    "/swagger-ui.html",
    "/logout"
  };

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        // 1. Vypneme CSRF ochranu, keďže používame stateless JWT autentifikáciu
        .csrf(AbstractHttpConfigurer::disable)

        // 2. Definujeme autorizačné pravidlá pre HTTP požiadavky
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(PUBLIC_PATHS)
                    .permitAll()
                    .anyRequest() // Vsetky ostatne cesty musia byt autorizovane
                    .authenticated())

        // 3. Povieme Springu, aby sa správal, ako OAuth2 Resource Server a očakával a validoval JWT
        // tokeny.
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))

        // 4. Nastavíme session management na STATELESS. Nevytvárame žiadne HTTP session na serveri,
        // spoliehame sa výlučne na token.
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .build();
  }
}
