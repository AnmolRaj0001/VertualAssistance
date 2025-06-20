package com.example.formsubmission;

import jakarta.validation.Valid;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
    
    
    public static class OtpRequest { // Make sure this is public static
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    
    public static class OtpVerificationRequest { // Make sure this is public static
        private String email;
        private String otp;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) { // Corrected: Removed extra 'void'
            this.email = email;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }
    }
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
    
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody OtpRequest otpRequest) {
        try {
            String email = otpRequest.getEmail();
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required.");
            }

            // Call async method and get the Future
            CompletableFuture<String> otpFuture = emailService.sendOtpEmail(email);
            
            // Wait for the Future to complete and get the result
            String otp = otpFuture.get(); // This will block until the async method finishes

            if (otp != null) {
                return ResponseEntity.ok("OTP sent successfully to " + email);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP.");
            }
        } catch (InterruptedException e) {
            // Handle interruption if the current thread is interrupted while waiting
            Thread.currentThread().interrupt(); // Restore the interrupted status
            System.err.println("Error sending OTP (interrupted): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error sending OTP: " + e.getMessage());
        } catch (ExecutionException e) {
            // Handle exceptions that occurred during the execution of the async task
            System.err.println("Error sending OTP (execution): " + e.getCause().getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error sending OTP: " + e.getCause().getMessage());
        } catch (Exception e) {
            System.err.println("Error sending OTP: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error sending OTP: " + e.getMessage());
        }
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerificationRequest verificationRequest) {
        try {
            String email = verificationRequest.getEmail();
            String otp = verificationRequest.getOtp();

            if (email == null || email.isEmpty() || otp == null || otp.isEmpty()) {
                return ResponseEntity.badRequest().body("Email and OTP are required.");
            }

            if (emailService.verifyOtp(email, otp)) {
                return ResponseEntity.ok("OTP verified successfully!");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP or OTP expired.");
            }
        } catch (Exception e) {
            System.err.println("Error verifying OTP: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error verifying OTP: " + e.getMessage());
        }
    }
}