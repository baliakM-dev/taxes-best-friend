package sk.taxesBestFriend.api_gateway.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  // Načítanie adresy z application.yml, kam sa má používateľ vrátiť po odhlásení.
  @Value("${keycloak.logout.post_logout_redirect_uri}")
  private String postLogoutRedirectUri;

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(
      ServerHttpSecurity http, ReactiveClientRegistrationRepository clientRegistrationRepository) {
    http.authorizeExchange(
            exchange ->
                exchange
                    // Definovanie verejných ciest, ktoré nevyžadujú prihlásenie.
                    .pathMatchers("/api/public/**", "/api/admin/**")
                    .permitAll()
                    // Všetky ostatné požiadavky musia byť autentifikované.
                    .anyExchange()
                    .authenticated())
        // Zapnutie štandardného prihlasovacieho procesu cez OAuth2/OIDC.
        .oauth2Login(Customizer.withDefaults())
        //
        // === TOTO JE FINÁLNA A SPRÁVNA KONFIGURÁCIA ODHLÁSENIA ===
        //
        .logout(
            logout ->
                logout.logoutSuccessHandler(
                    oidcLogoutSuccessHandler(clientRegistrationRepository)));

    return http.build();
  }

  /**
   * Vytvára a konfiguruje handler pre OIDC odhlásenie. Tento handler zabezpečí, že Spring Security
   * najprv zmaže lokálnu session a až potom presmeruje používateľa na odhlasovaciu stránku
   * Keycloaku.
   */
  private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(
      ReactiveClientRegistrationRepository clientRegistrationRepository) {
    // Vytvorenie štandardného OIDC logout handlera.
    OidcClientInitiatedServerLogoutSuccessHandler oidcLogoutSuccessHandler =
        new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);

    // Nastavenie URI z application.yml, kam sa má používateľ vrátiť po odhlásení z Keycloaku.
    oidcLogoutSuccessHandler.setPostLogoutRedirectUri(
        String.valueOf(URI.create(postLogoutRedirectUri)));

    return oidcLogoutSuccessHandler;
  }
}
