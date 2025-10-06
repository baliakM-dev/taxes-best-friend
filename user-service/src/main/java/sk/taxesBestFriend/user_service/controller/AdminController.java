package sk.taxesBestFriend.user_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

  public AdminController() {}

  @GetMapping("/dashboard")
  public String getAdminDashboard() {
    return "Admin Dashboard - prístup povolený!";
  }
}
