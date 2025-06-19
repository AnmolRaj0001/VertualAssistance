package com.example.formsubmission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail; // The email address configured in application.properties

    @Value("${app.admin.email}")
    private String adminEmail; // The admin email address

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
}