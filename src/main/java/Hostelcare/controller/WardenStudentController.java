
package Hostelcare.controller;
import java.util.List;
import Hostelcare.entity.User;
import Hostelcare.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WardenStudentController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public WardenStudentController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Open Add Student page
    @GetMapping("/warden/students/new")
    public String showAddStudentPage(HttpSession session) {

        if (!"WARDEN".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }

        return "student-create";
    }

    // Save new student account
    @PostMapping("/warden/students")
    public String addStudent(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!"WARDEN".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }

        name = name.trim();
        email = email.trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            model.addAttribute("error", "Please fill in all fields.");
            return "student-create";
        }

        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error",
                    "An account with this email already exists.");
            return "student-create";
        }

        User student = new User();

        student.setName(name);
        student.setEmail(email);
        student.setPassword(passwordEncoder.encode(password));
        student.setRole("STUDENT");
        student.setActive(true);

        userRepository.save(student);

        redirectAttributes.addFlashAttribute(
                "success", "Student account created successfully!");

        return "redirect:/admin";
    }
    @GetMapping("/warden/students")
    public String viewStudents(HttpSession session, Model model) {

        if (!"WARDEN".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }

        List<User> students = userRepository.findAll()
                .stream()
                .filter(user -> "STUDENT".equals(user.getRole()))
                .toList();

        model.addAttribute("students", students);

        return "students";
    }
}

