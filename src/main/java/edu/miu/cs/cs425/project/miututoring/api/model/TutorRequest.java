package edu.miu.cs.cs425.project.miututoring.api.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class TutorRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requestId;

    @ManyToOne
    @JoinColumn(name = "section")
    private Section section;

    @OneToOne
    @JoinColumn(name = "student")
    @NotNull(message = "User Id can not be null")
    private Student userId;

    private Status status;
    private String experience;

    public enum Status {
        PENDING, ACCEPTED, REJECTED;
    }

    public TutorRequest() {
    }

    public TutorRequest(Section section, Student student, Status status, String experience) {
        this.section = section;
        this.userId = student;
        this.status = Status.PENDING;
        this.experience = experience;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public Student getUserId() {
        return userId;
    }

    public void setUserId(Student userId) {
        this.userId = userId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    @Override
    public String toString() {
        return String.format(
                "TutorRequest [section=%s, userId=%s, status=%s, experience=%s]",
                this.section,
                this.userId,
                this.status,
                this.experience);
    }
}
