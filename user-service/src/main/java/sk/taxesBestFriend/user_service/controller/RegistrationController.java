package sk.taxesBestFriend.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class RegistrationController {

  // Nepotrebujeme teraz žiadne závislosti, len vrátime text
  public RegistrationController() {}

  // ZMENA: Zmenili sme @PostMapping na @GetMapping pre jednoduché testovanie v prehliadači
  @GetMapping("/register")
  public ResponseEntity<String> testRegisterEndpoint() {
    // Jednoducho vrátime text, aby sme vedeli, že sme sa sem dostali
    return ResponseEntity.ok("Registracny endpoint v lllll USER-SERVICE funguje!");
  }
}
