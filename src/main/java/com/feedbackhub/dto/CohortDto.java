package com.feedbackhub.dto;
import java.util.List;
public class CohortDto {
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
}
