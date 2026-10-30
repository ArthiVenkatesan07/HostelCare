
package Hostelcare.controller;
import Hostelcare.entity.User;
import Hostelcare.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            HttpSession session,
            Model model) {

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null ||
        	    !user.isActive() ||
        	    !passwordEncoder.matches(password, user.getPassword()) ||
        	    !role.equals(user.getRole())) {

            model.addAttribute("error",
                    "Invalid email, password, or role.");
            return "login";
        }

        session.setAttribute("userId", user.getId());
        session.setAttribute("userRole", user.getRole());
        session.setAttribute("userEmail", user.getEmail());

        if ("WARDEN".equals(user.getRole())) {
            return "redirect:/admin";
        }

        return "redirect:/student/complaints";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
