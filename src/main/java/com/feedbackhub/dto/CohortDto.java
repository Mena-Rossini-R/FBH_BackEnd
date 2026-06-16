package com.feedbackhub.dto;
import java.util.List;
public class CohortDto {

    // ── Excel upload result ──────────────────────────────────────────────────
    public static class UploadResult {
        private List<UserDto.Response> created, updated;
        private List<String> errors;
        private int createdCount, updatedCount, errorCount;
        public List<UserDto.Response> getCreated()    { return created; }
        public List<UserDto.Response> getUpdated()    { return updated; }
        public List<String>   getErrors()             { return errors; }
        public int getCreatedCount()                  { return createdCount; }
        public int getUpdatedCount()                  { return updatedCount; }
        public int getErrorCount()                    { return errorCount; }
        public void setCreated(List<UserDto.Response> v) { this.created      = v; }
        public void setUpdated(List<UserDto.Response> v) { this.updated      = v; }
        public void setErrors(List<String> v)            { this.errors       = v; }
        public void setCreatedCount(int v)               { this.createdCount = v; }
        public void setUpdatedCount(int v)               { this.updatedCount = v; }
        public void setErrorCount(int v)                 { this.errorCount   = v; }
    }

    // ── Cohort structure ─────────────────────────────────────────────────────
    public static class StudentInfo {
        private Long id; private String fullName, email, phone, department;
        public Long   getId()          { return id; }
        public String getFullName()    { return fullName; }
        public String getEmail()       { return email; }
        public String getPhone()       { return phone; }
        public String getDepartment()  { return department; }
        public void setId(Long v)           { this.id = v; }
        public void setFullName(String v)   { this.fullName = v; }
        public void setEmail(String v)      { this.email = v; }
        public void setPhone(String v)      { this.phone = v; }
        public void setDepartment(String v) { this.department = v; }
    }

    public static class PodInfo {
        private String podName;
        private List<StudentInfo> students;
        public String getPodName()                    { return podName; }
        public List<StudentInfo> getStudents()        { return students; }
        public void setPodName(String v)              { this.podName = v; }
        public void setStudents(List<StudentInfo> v)  { this.students = v; }
    }

    public static class CohortInfo {
        private String cohortName;
        private List<PodInfo> pods;
        private int studentCount;
        public String getCohortName()             { return cohortName; }
        public List<PodInfo> getPods()            { return pods; }
        public int getStudentCount()              { return studentCount; }
        public void setCohortName(String v)       { this.cohortName = v; }
        public void setPods(List<PodInfo> v)      { this.pods = v; }
        public void setStudentCount(int v)        { this.studentCount = v; }
    }

    public static class Structure {
        private List<CohortInfo> cohorts;
        private List<StudentInfo> unassigned;
        public List<CohortInfo> getCohorts()          { return cohorts; }
        public List<StudentInfo> getUnassigned()      { return unassigned; }
        public void setCohorts(List<CohortInfo> v)    { this.cohorts = v; }
        public void setUnassigned(List<StudentInfo> v){ this.unassigned = v; }
    }

    // ── Request bodies ───────────────────────────────────────────────────────
    public static class AddTraineeRequest {
        private String fullName, email, phone, department, cohortName, podName;
        public String getFullName()    { return fullName; }
        public String getEmail()       { return email; }
        public String getPhone()       { return phone; }
        public String getDepartment()  { return department; }
        public String getCohortName()  { return cohortName; }
        public String getPodName()     { return podName; }
        public void setFullName(String v)   { this.fullName = v; }
        public void setEmail(String v)      { this.email = v; }
        public void setPhone(String v)      { this.phone = v; }
        public void setDepartment(String v) { this.department = v; }
        public void setCohortName(String v) { this.cohortName = v; }
        public void setPodName(String v)    { this.podName = v; }
    }

    public static class AssignRequest {
        private Long userId; private String podName, cohortName;
        public Long   getUserId()       { return userId; }
        public String getPodName()      { return podName; }
        public String getCohortName()   { return cohortName; }
        public void setUserId(Long v)       { this.userId = v; }
        public void setPodName(String v)    { this.podName = v; }
        public void setCohortName(String v) { this.cohortName = v; }
    }
}
