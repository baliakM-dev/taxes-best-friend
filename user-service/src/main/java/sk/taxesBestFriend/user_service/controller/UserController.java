package sk.taxesBestFriend.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users") // Všetky endpointy v tejto triede budú začínať na /api/users
@Tag(name = "User API", description = "Endpointy pre prácu s dátami používateľa")
public class UserController {

  /**
   * Endpoint, ktorý vráti detaily o aktuálne prihlásenom používateľovi. Dáta sú extrahované priamo
   * z JWT tokenu, ktorý bol poslaný v Authorization hlavičke.
   *
   * @param principal Objekt reprezentujúci dekódovaný JWT token, vstreknutý Spring Security.
   * @return Mapa obsahujúca všetky "claims" (dáta) z tokenu.
   */
  @GetMapping("/me")
  @Operation(
      summary = "Vráti detaily prihláseného používateľa",
      security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<Map<String, Object>> getCurrentUser(
      @AuthenticationPrincipal Jwt principal) {
    // Z JWT tokenu si jednoducho vytiahneme všetky jeho "claims" (dáta)
    Map<String, Object> claims = principal.getClaims();

    // Vrátime ich ako JSON odpoveď s HTTP statusom 200 OK
    return ResponseEntity.ok(claims);
  }
}
