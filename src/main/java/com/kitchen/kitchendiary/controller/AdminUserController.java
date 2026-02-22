package com.kitchen.kitchendiary.controller;

import com.kitchen.kitchendiary.entities.User;
import com.kitchen.kitchendiary.repositories.UserRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminUserController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AdminUserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping("/admin/users")
  public String usersPage(Model model) {
    List<User> users = userRepository.findAll().stream()
        .sorted(Comparator.comparing(User::getCreatedAt).reversed())
        .toList();
    model.addAttribute("users", users);
    return "admin/users";
  }

  @PostMapping("/admin/users")
  public String createUser(
      @RequestParam String name,
      @RequestParam String email,
      @RequestParam String password,
      @RequestParam(defaultValue = "USER") String role,
      RedirectAttributes redirectAttributes) {
    String normalizedEmail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    String normalizedName = name == null ? "" : name.trim();
    String normalizedRole = role == null ? "USER" : role.trim().toUpperCase(Locale.ROOT);

    if (normalizedName.isBlank()) {
      redirectAttributes.addAttribute("error", "Name is required");
      return "redirect:/admin/users";
    }
    if (normalizedEmail.isBlank()) {
      redirectAttributes.addAttribute("error", "Email is required");
      return "redirect:/admin/users";
    }
    if (password == null || password.length() < 6) {
      redirectAttributes.addAttribute("error", "Password must be at least 6 characters");
      return "redirect:/admin/users";
    }
    if (!"USER".equals(normalizedRole) && !"ADMIN".equals(normalizedRole)) {
      redirectAttributes.addAttribute("error", "Role must be USER or ADMIN");
      return "redirect:/admin/users";
    }
    if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
      redirectAttributes.addAttribute("error", "Email already exists");
      return "redirect:/admin/users";
    }

    User user = new User();
    user.setName(normalizedName);
    user.setEmail(normalizedEmail);
    user.setPasswordHash(passwordEncoder.encode(password));
    user.setRole(normalizedRole);
    userRepository.save(user);

    redirectAttributes.addAttribute("message", "User created: " + normalizedEmail);
    return "redirect:/admin/users";
  }
}
