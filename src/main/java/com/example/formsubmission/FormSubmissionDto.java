package com.example.formsubmission;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class FormSubmissionDto {

    // Common fields for both forms
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+\\d{1,4}\\d{7,15}$", message = "Phone number must be in a valid international format")
    private String fullPhoneNumber; // Combines phoneCode and phoneNumber

    private String experience; // Optional

    // Fields specific to HirePopupForm
    private String lookingFor; // "A Job", "A Virtual Assistant", "Other Service"

    // Fields specific to Hero form (for "Book Up Free Demo")
    private String jobRole; // "Virtual Assistant", "Executive Assistant", etc.

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullPhoneNumber() {
        return fullPhoneNumber;
    }

    public void setFullPhoneNumber(String fullPhoneNumber) {
        this.fullPhoneNumber = fullPhoneNumber;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getLookingFor() {
        return lookingFor;
    }

    public void setLookingFor(String lookingFor) {
        this.lookingFor = lookingFor;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    @Override
    public String toString() {
        return "FormSubmissionDto{" +
               "name='" + name + '\'' +
               ", email='" + email + '\'' +
               ", fullPhoneNumber='" + fullPhoneNumber + '\'' +
               ", experience='" + experience + '\'' +
               (lookingFor != null ? ", lookingFor='" + lookingFor + '\'' : "") +
               (jobRole != null ? ", jobRole='" + jobRole + '\'' : "") +
               '}';
    }
}