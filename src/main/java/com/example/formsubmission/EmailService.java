package com.example.formsubmission;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail; // The email address configured in application.properties

    @Value("${app.admin.email}")
    private String adminEmail; // The admin email address
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final Map<String, OtpDetails> otpStorage = new HashMap<>();

    /**
     * Sends a confirmation email to the user.
     * @param toEmail The recipient's email address.
     * @param name The user's name.
     */
    @Async
    public void sendUserConfirmationEmail(String toEmail, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Form Submission Confirmation");
        message.setText("Dear " + name + ",\n\n"
                      + "Thank you for submitting your form! We have received your application and will get back to you shortly.\n\n"
                      + "Best regards,\nYour Company Team");
        try {
            mailSender.send(message);
            System.out.println("User confirmation email sent to: " + toEmail);
        } catch (MailException e) {
            System.err.println("Error sending user confirmation email to " + toEmail + ": " + e.getMessage());
            // Log the error more robustly in a real application
        }
    }

    /**
     * Sends an email to the admin with the form submission details.
     * @param submission The FormSubmissionDto containing all details.
     */
    @Async
    public void sendAdminNotificationEmail(FormSubmissionDto submission) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(adminEmail);
        message.setSubject("New Form Submission Received!");
        message.setText("A new form has been submitted with the following details:\n\n"
                      + "Name: " + submission.getName() + "\n"
                      + "Email: " + submission.getEmail() + "\n"
                      + "Phone Number: " + submission.getFullPhoneNumber() + "\n"
                      + "Experience: " + (submission.getExperience() != null && !submission.getExperience().isEmpty() ? submission.getExperience() : "N/A") + "\n"
                      + (submission.getLookingFor() != null ? "Looking For: " + submission.getLookingFor() + "\n" : "")
                      + (submission.getJobRole() != null ? "Job Role: " + submission.getJobRole() + "\n" : "")
                      + "\n"
                      + "Please review the details and follow up as necessary.");
        try {
            mailSender.send(message);
            System.out.println("Admin notification email sent to: " + adminEmail);
        } catch (MailException e) {
            System.err.println("Error sending admin notification email: " + e.getMessage());
            // Log the error more robustly
        }
    }
    
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generates a 6-digit number
        return String.valueOf(otp);
    }

    /**
     * Sends an OTP to the specified email address and stores it temporarily.
     * @param toEmail The recipient's email address.
     * @return The generated OTP.
     */
    @Async
    public String sendOtpEmail(String toEmail) {
        String otp = generateOtp();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Your OTP for Verification");
        message.setText("Your One-Time Password (OTP) is: " + otp + "\n\n"
                      + "This OTP is valid for 5 minutes. Please do not share it with anyone.\n\n"
                      + "Best regards,\nYour Company Team");
        try {
            mailSender.send(message);
            otpStorage.put(toEmail, new OtpDetails(otp, System.currentTimeMillis()));
            logger.info("OTP sent to: {}", toEmail); // Use logger
            return otp;
        } catch (MailException e) {
            logger.error("Error sending OTP email to {}: {}", toEmail, e.getMessage(), e); // Log full stack trace
            // You could rethrow a custom exception here if you want specific error handling upstream
            // throw new CustomEmailSendException("Failed to send OTP email", e);
            return null; // Keep returning null if controller expects it
        }
    }

    /**
     * Verifies the provided OTP against the stored OTP for the given email.
     * @param email The email address for which OTP was sent.
     * @param otp The OTP to verify.
     * @return True if OTP matches, false otherwise.
     */
    public boolean verifyOtp(String email, String otp) {
        OtpDetails storedDetails = otpStorage.get(email);
        if (storedDetails != null && storedDetails.getOtp().equals(otp)) {
            // Check expiration (e.g., 5 minutes = 300,000 milliseconds)
            if (System.currentTimeMillis() - storedDetails.getTimestamp() <= 300000) { // 5 minutes
                otpStorage.remove(email); // Invalidate OTP after successful verification
                return true;
            }
        }
        // If OTP doesn't match, is null, or expired, remove it to prevent brute-force
        if (storedDetails != null) {
            otpStorage.remove(email);
            logger.warn("OTP for {} either expired or was incorrect. Removed from storage.", email);
        } else {
            logger.warn("Attempt to verify non-existent or already used OTP for {}", email);
        }
        return false;
    }



}