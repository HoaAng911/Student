package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue
    @UuidGenerator   // Hibernate 6+
    @Column(columnDefinition = "UNIQUEIDENTIFIER",
            updatable = false,
            nullable = false)
    private UUID id;

    @Column(columnDefinition = "UNIQUEIDENTIFIER")
	private UUID user_id;
    
    @Column(name = "code", length = 20)
    private String code;
	
    @Column(name = "full_name", length = 100)
    private String fullname;
    
    @Column(name = "date_of_birth")
	private LocalDateTime date_of_birth;
    
    @Column(name = "gender", length = 10)
    private String gender;

	@Column(name = "personal_identification_number", length = 20)
    private String personal_identification_number;

    @Column(name = "date_of_issue")
    private LocalDateTime date_of_issue;

	@Column(name = "card_place", length = 100)
    private String card_place;

	@Column(name = "address", length = 300)
    private String address;

    @Column(name = "current_address", length = 300)
    private String current_address;

    @Column(columnDefinition = "UNIQUEIDENTIFIER")
    private UUID academic_year_year;
    
    @Column(columnDefinition = "UNIQUEIDENTIFIER")
    private UUID department_id;

    @Column(columnDefinition = "UNIQUEIDENTIFIER")
    private UUID major_id;
    
    @Column(columnDefinition = "UNIQUEIDENTIFIER")
    private UUID training_program_id;

    @Column(name = "status", length = 50)
    private String status;

    @Column(columnDefinition = "UNIQUEIDENTIFIER")
    private UUID student_classe_id;

    @Column(name = "admission_year")
    private LocalDateTime admission_year;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    @Column(name = "is_active")
    private Boolean isActive;
    

    public Student() {}
    
    public Student(UUID user_id, String code, String fullname, LocalDateTime date_of_birth, String gender,
                   String personal_identification_number, LocalDateTime date_of_issue, String card_place, String address, String current_address, UUID academic_year_year, UUID department_id,  UUID major_id, UUID training_program_id, String status, UUID student_classe_id, LocalDateTime admission_year, LocalDateTime createdAt, LocalDateTime updatedAt, UUID createdBy, UUID updatedBy, LocalDateTime deletedAt, UUID deletedBy, Boolean isActive) {    
        this.user_id = user_id;
        this.code = code;
        this.fullname = fullname;
        this.date_of_birth = date_of_birth;
        this.gender = gender;
        this.personal_identification_number = personal_identification_number;
        this.date_of_issue = date_of_issue;
        this.card_place = card_place;
        this.address = address;
        this.current_address = current_address;
        this.academic_year_year = academic_year_year;
        this.department_id = department_id;
        this.major_id = major_id;
        this.training_program_id = training_program_id;
        this.status = status;
        this.student_classe_id = student_classe_id;
        this.admission_year = admission_year;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.deletedAt = deletedAt;
        this.deletedBy = deletedBy;
        this.isActive = isActive;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUser_id() {
        return user_id;
    }

    public void setUser_id(UUID user_id) {
        this.user_id = user_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public LocalDateTime getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(LocalDateTime date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPersonal_identification_number() {
        return personal_identification_number;
    }

    public void setPersonal_identification_number(String personal_identification_number) {
        this.personal_identification_number = personal_identification_number;
    }

    public LocalDateTime getDate_of_issue() {
        return date_of_issue;
    }

    public void setDate_of_issue(LocalDateTime date_of_issue) {
        this.date_of_issue = date_of_issue;
    }

    public String getCard_place() {
        return card_place;
    }

    public void setCard_place(String card_place) {
        this.card_place = card_place;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCurrent_address() {
        return current_address;
    }

    public void setCurrent_address(String current_address) {
        this.current_address = current_address;
    }

    public UUID getAcademic_year_year() {
        return academic_year_year;
    }

    public void setAcademic_year_year(UUID academic_year_year) {
        this.academic_year_year = academic_year_year;
    }

    public UUID getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(UUID department_id) {
        this.department_id = department_id;
    }

    public UUID getMajor_id() {
        return major_id;
    }

    public void setMajor_id(UUID major_id) {
        this.major_id = major_id;
    }

    public UUID getTraining_program_id() {
        return training_program_id;
    }

    public void setTraining_program_id(UUID training_program_id) {
        this.training_program_id = training_program_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getStudent_classe_id() {
        return student_classe_id;
    }

    public void setStudent_classe_id(UUID student_classe_id) {
        this.student_classe_id = student_classe_id;
    }

    public LocalDateTime getAdmission_year() {
        return admission_year;
    }

    public void setAdmission_year(LocalDateTime admission_year) {
        this.admission_year = admission_year;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UUID updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    
}