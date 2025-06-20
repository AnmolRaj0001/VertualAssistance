package com.example.formsubmission;

class OtpDetails {
    private String otp;
    private long timestamp;
    // constructor, getters
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
    
	public OtpDetails(String otp, long timestamp) {
        this.otp = otp;
        this.timestamp = timestamp;
    }
}