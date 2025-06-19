package com.example.formsubmission;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FormController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/submit-form")
    public ResponseEntity<String> submitForm(@Valid @RequestBody FormSubmissionDto submission) {
        try {
            System.out.println("Received form submission: " + submission.toString());

            // Send confirmation email to the user
            emailService.sendUserConfirmationEmail(submission.getEmail(), submission.getName());

            // Send notification email to the admin
            emailService.sendAdminNotificationEmail(submission);

            return ResponseEntity.ok("Form submitted successfully!");
        } catch (Exception e) {
            System.err.println("Error processing form submission: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error submitting form: " + e.getMessage());
        }
    }
}