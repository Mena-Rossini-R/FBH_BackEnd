package com.feedbackhub.dto;

public class AuthDto {

    public static class LoginRequest {
        private String email;
        private String password;
        public LoginRequest() {}
        public String getEmail()    { return email; }
        public String getPassword() { return password; }
        public void setEmail(String email)       { this.email    = email; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        private String token;
        private String role;
        private String fullName;
        private Long   userId;
        private String email;
        private String podName;
        private String cohortName;
        public AuthResponse() {}
        public String getToken()      { return token; }
        public String getRole()       { return role; }
        public String getFullName()   { return fullName; }
        public Long   getUserId()     { return userId; }
        public String getEmail()      { return email; }
        public String getPodName()    { return podName; }
        public String getCohortName() { return cohortName; }
        public void setToken(String token)           { this.token      = token; }
        public void setRole(String role)             { this.role       = role; }
        public void setFullName(String fullName)     { this.fullName   = fullName; }
        public void setUserId(Long userId)           { this.userId     = userId; }
        public void setEmail(String email)           { this.email      = email; }
        public void setPodName(String podName)       { this.podName    = podName; }
        public void setCohortName(String cohortName) { this.cohortName = cohortName; }
    }

    public static class RegisterRequest {
        private String fullName;
        private String email;
        private String password;
        private String role;
        private String department;
        private String podName;
        private String cohortName;
        public RegisterRequest() {}
        public String getFullName()   { return fullName; }
        public String getEmail()      { return email; }
        public String getPassword()   { return password; }
        public String getRole()       { return role; }
        public String getDepartment() { return department; }
        public String getPodName()    { return podName; }
        public String getCohortName() { return cohortName; }
        public void setFullName(String fullName)     { this.fullName   = fullName; }
        public void setEmail(String email)           { this.email      = email; }
        public void setPassword(String password)     { this.password   = password; }
        public void setRole(String role)             { this.role       = role; }
        public void setDepartment(String department) { this.department = department; }
        public void setPodName(String podName)       { this.podName    = podName; }
        public void setCohortName(String cohortName) { this.cohortName = cohortName; }
    }
}
