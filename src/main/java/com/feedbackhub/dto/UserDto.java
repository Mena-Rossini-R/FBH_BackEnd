package com.feedbackhub.dto;

public class UserDto {
    public static class Response {
        private Long id;
        private String fullName, email, role, podName, cohortName, phone, department;
        private boolean active;
        private double latestScore;
        private String trendDirection;

        public Response() {
        }

        public Long getId() {
            return id;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }

        public String getRole() {
            return role;
        }

        public String getPodName() {
            return podName;
        }

        public String getCohortName() {
            return cohortName;
        }

        public String getPhone() {
            return phone;
        }

        public String getDepartment() {
            return department;
        }

        public boolean isActive() {
            return active;
        }

        public double getLatestScore() {
            return latestScore;
        }

        public String getTrendDirection() {
            return trendDirection;
        }

        public void setId(Long v) {
            this.id = v;
        }

        public void setFullName(String v) {
            this.fullName = v;
        }

        public void setEmail(String v) {
            this.email = v;
        }

        public void setRole(String v) {
            this.role = v;
        }

        public void setPodName(String v) {
            this.podName = v;
        }

        public void setCohortName(String v) {
            this.cohortName = v;
        }

        public void setPhone(String v) {
            this.phone = v;
        }

        public void setDepartment(String v) {
            this.department = v;
        }

        public void setActive(boolean v) {
            this.active = v;
        }

        public void setLatestScore(double v) {
            this.latestScore = v;
        }

        public void setTrendDirection(String v) {
            this.trendDirection = v;
        }
    }

    public void setId(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setId'");
    }

    public void setFullName(String fullName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setFullName'");
    }

    public void setEmail(String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setEmail'");
    }

    public void setLatestScore(Double double1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setLatestScore'");
    }
}
