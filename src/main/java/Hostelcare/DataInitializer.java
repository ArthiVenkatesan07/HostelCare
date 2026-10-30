
package Hostelcare;

import Hostelcare.entity.User;
import Hostelcare.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner createDemoUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Student account
            User student = userRepository
                    .findByEmail("student@hostelcare.com")
                    .orElseGet(User::new);

            student.setName("Demo Student");
            student.setEmail("student@hostelcare.com");
            student.setRole("STUDENT");
            student.setActive(true);

            if (student.getPassword() == null ||
                !passwordEncoder.matches(
                        "Student@123", student.getPassword())) {

                student.setPassword(
                        passwordEncoder.encode("Student@123"));
            }

            userRepository.save(student);

            // Warden account
            User warden = userRepository
                    .findByEmail("warden@hostelcare.com")
                    .orElseGet(User::new);

            warden.setName("Demo Warden");
            warden.setEmail("warden@hostelcare.com");
            warden.setRole("WARDEN");
            warden.setActive(true);

            if (warden.getPassword() == null ||
                !passwordEncoder.matches(
                        "Warden@123", warden.getPassword())) {

                warden.setPassword(
                        passwordEncoder.encode("Warden@123"));
            }

            userRepository.save(warden);
        };
    }
}
