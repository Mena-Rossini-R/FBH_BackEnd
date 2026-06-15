package com.feedbackhub.entity;

import com.feedbackhub.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private String department;
    private String phone;

    // Pod assignment for trainees
    private String podName;
    // Cohort assignment for trainees
    private String cohortName;

    @Column(nullable = false)
    private boolean active = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {}

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override public String  getUsername()              { return email; }
    @Override public boolean isAccountNonExpired()      { return true; }
    @Override public boolean isAccountNonLocked()       { return true; }
    @Override public boolean isCredentialsNonExpired()  { return true; }
    @Override public boolean isEnabled()                { return active; }

    public Long          getId()          { return id; }
    public String        getFullName()    { return fullName; }
    public String        getEmail()       { return email; }
    public String        getPassword()    { return password; }
    public UserRole      getRole()        { return role; }
    public String        getDepartment()  { return department; }
    public String        getPhone()       { return phone; }
    public String        getPodName()     { return podName; }
    public String        getCohortName()  { return cohortName; }
    public boolean       isActive()       { return active; }
    public LocalDateTime getCreatedAt()   { return createdAt; }
    public LocalDateTime getUpdatedAt()   { return updatedAt; }

    public void setId(Long id)                        { this.id          = id; }
    public void setFullName(String fullName)          { this.fullName    = fullName; }
    public void setEmail(String email)                { this.email       = email; }
    public void setPassword(String password)          { this.password    = password; }
    public void setRole(UserRole role)                { this.role        = role; }
    public void setDepartment(String department)      { this.department  = department; }
    public void setPhone(String phone)                { this.phone       = phone; }
    public void setPodName(String podName)            { this.podName     = podName; }
    public void setCohortName(String cohortName)      { this.cohortName  = cohortName; }
    public void setActive(boolean active)             { this.active      = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt   = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt   = updatedAt; }
}
