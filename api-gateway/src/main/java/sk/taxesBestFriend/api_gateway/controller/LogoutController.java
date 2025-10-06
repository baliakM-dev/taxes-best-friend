package sk.taxesBestFriend.api_gateway.controller;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

/** Controller pre správu odhlásenia používateľa. Tento súbor patrí do API Gateway. */
@RestController
public class LogoutController {

  // Načítanie issuer-uri z application.yml
  @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
  private String issuerUri;

  // Načítanie post-logout-uri z application.yml
  @Value("${keycloak.logout.post_logout_redirect_uri}")
  private String postLogoutRedirectUri;

  /**
   * Endpoint pre odhlásenie. Ukončí lokálnu session a presmeruje na Keycloak pre globálne
   * odhlásenie.
   *
   * @param oidcUser Principal objekt, z ktorého získame id_token.
   * @param exchange ServerWebExchange pre prácu so session.
   * @return Presmerovanie na odhlasovaciu stránku Keycloaku.
   */
  @GetMapping("/logout")
  public Mono<ResponseEntity<Void>> logout(
      @AuthenticationPrincipal OidcUser oidcUser, ServerWebExchange exchange) {
    // Zostavenie URL pre odhlásenie v Keycloaku
    URI logoutUri =
        UriComponentsBuilder.fromUriString(issuerUri + "/protocol/openid-connect/logout")
            .queryParam("id_token_hint", oidcUser.getIdToken().getTokenValue())
            .queryParam("post_logout_redirect_uri", postLogoutRedirectUri)
            .build()
            .toUri();

    // Ukončenie lokálnej session a presmerovanie
    return exchange
        .getSession()
        .flatMap(
            session -> {
              session.invalidate();
              // OPRAVA: Pridaný <Void> "type hint", aby sa zhodoval návratový typ metódy.
              return Mono.just(ResponseEntity.status(302).location(logoutUri).<Void>build());
            });
  }
}
