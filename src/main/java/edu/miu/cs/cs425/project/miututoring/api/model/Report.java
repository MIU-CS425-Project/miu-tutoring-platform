package edu.miu.cs.cs425.project.miututoring.api.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reportId;

    @ManyToOne
    @JoinColumn(name = "student")
    @NotNull(message = "*Student is Required")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "tutuorial_group")
    @NotNull(message = "*Tutorial Group is Required")
    private TutorialGroup tutorialGroup;

    @OneToOne
    @JoinColumn(name = "course")
    private Course course;

    @NotNull(message = "*Report is Required")
    private String reportText;


    public Report() {
    }
    public Report(Student student, TutorialGroup tutorialGroup, Course course, String report){
        this.student = student;
        this.tutorialGroup = tutorialGroup;
        this.course = course;
        this.reportText = report;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public TutorialGroup getTutorialGroup() {
        return tutorialGroup;
    }

    public void setTutorialGroup(TutorialGroup tutorialGroup) {
        this.tutorialGroup = tutorialGroup;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getReport() {
        return reportText;
    }

    public void setReport(String report) {
        this.reportText = report;
    }

    @Override
    public String toString() {
        return String.format(
                "Report [reportId=%s, student=%s, tutorialGroup=%s, course=%s, report=%s]",
                this.reportId,
                this.student.getFirstName()+" "+this.student.getLastName(),
                this.tutorialGroup,
                this.course,
                this.reportText);
    }
}
