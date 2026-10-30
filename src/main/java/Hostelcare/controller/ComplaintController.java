
package Hostelcare.controller;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import Hostelcare.entity.Complaint;
import Hostelcare.repository.ComplaintRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ComplaintController {

    private final ComplaintRepository complaintRepository;

    public ComplaintController(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }


    @GetMapping("/complaints/new")
    public String showComplaintForm(
            Model model,
            jakarta.servlet.http.HttpSession session) {

        if (!"STUDENT".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }

        model.addAttribute("complaint", new Complaint());
        return "complaint-form";
    }


    @PostMapping("/complaints")
    public String submitComplaint(
            @ModelAttribute Complaint complaint,
            jakarta.servlet.http.HttpSession session) {

        if (!"STUDENT".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }

        Long studentId = (Long) session.getAttribute("userId");
        complaint.setStudentId(studentId);

        complaintRepository.save(complaint);

        return "redirect:/complaints/success";
    }

    @GetMapping("/complaints/success")
    public String complaintSuccess(
            Model model,
            HttpSession session) {

        if (!"STUDENT".equals(session.getAttribute("userRole"))) {
            return "redirect:/login";
        }

        return "success";
    }
    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "HostelCare controller is working!";
    }


@GetMapping("/complaints")
public String viewComplaints(
        Model model,
        jakarta.servlet.http.HttpSession session) {

    if (!"WARDEN".equals(session.getAttribute("userRole"))) {
        return "redirect:/login";
    }

    model.addAttribute("complaints", complaintRepository.findAll());
    return "complaints";
}



@GetMapping("/complaints/update-status")
public String updateComplaintStatus(
        @org.springframework.web.bind.annotation.RequestParam Long id,
        @org.springframework.web.bind.annotation.RequestParam String status,
        jakarta.servlet.http.HttpSession session) {

    if (!"WARDEN".equals(session.getAttribute("userRole"))) {
        return "redirect:/login";
    }

    Complaint complaint = complaintRepository.findById(id).orElse(null);

    if (complaint != null &&
            ("PENDING".equals(status) ||
             "IN_PROGRESS".equals(status) ||
             "RESOLVED".equals(status))) {

        complaint.setStatus(status);
        complaintRepository.save(complaint);
    }

    return "redirect:/admin";
}

@GetMapping("/student/complaints")
public String viewStudentComplaints(
        Model model,
        jakarta.servlet.http.HttpSession session) {

    if (!"STUDENT".equals(session.getAttribute("userRole"))) {
        return "redirect:/login";
    }

    Long studentId = (Long) session.getAttribute("userId");

    model.addAttribute(
            "complaints",
            complaintRepository.findByStudentId(studentId)
    );

    return "student-complaints";
}


@GetMapping("/admin")
public String adminDashboard(
        Model model,
        jakarta.servlet.http.HttpSession session) {
	if (!"WARDEN".equals(session.getAttribute("userRole"))) {
	    return "redirect:/login";
	}

    java.util.List<Complaint> complaints = complaintRepository.findAll();

    long pending = complaints.stream()
            .filter(c -> "PENDING".equals(c.getStatus()))
            .count();

    long inProgress = complaints.stream()
            .filter(c -> "IN_PROGRESS".equals(c.getStatus()))
            .count();

    long resolved = complaints.stream()
            .filter(c -> "RESOLVED".equals(c.getStatus()))
            .count();

    model.addAttribute("complaints", complaints);
    model.addAttribute("total", complaints.size());
    model.addAttribute("pending", pending);
    model.addAttribute("inProgress", inProgress);
    model.addAttribute("resolved", resolved);

    return "admin";
}
@GetMapping("/complaints/feedback")
public String showFeedbackForm(
        @RequestParam Long id,
        Model model,
        HttpSession session) {

    if (!"STUDENT".equals(session.getAttribute("userRole"))) {
        return "redirect:/login";
    }

    Long studentId = (Long) session.getAttribute("userId");

    Complaint complaint = complaintRepository.findById(id).orElse(null);

    if (complaint == null
            || !studentId.equals(complaint.getStudentId())
            || !"RESOLVED".equals(complaint.getStatus())) {
        return "redirect:/student/complaints";
    }

    model.addAttribute("complaint", complaint);
    return "feedback";
}
@PostMapping("/complaints/feedback")
public String submitFeedback(
        @RequestParam Long id,
        @RequestParam Integer rating,
        @RequestParam(required = false) String comment,
        jakarta.servlet.http.HttpSession session) {

    if (!"STUDENT".equals(session.getAttribute("userRole"))) {
        return "redirect:/login";
    }

    Long studentId = (Long) session.getAttribute("userId");

    Complaint complaint = complaintRepository.findById(id).orElse(null);

    if (complaint == null
            || !studentId.equals(complaint.getStudentId())
            || !"RESOLVED".equals(complaint.getStatus())) {
        return "redirect:/student/complaints";
    }

    if (rating < 1 || rating > 5) {
        return "redirect:/student/complaints";
    }

    complaint.setFeedbackRating(rating);
    complaint.setFeedbackComment(comment);
    complaintRepository.save(complaint);

    return "redirect:/student/complaints";
}
}
