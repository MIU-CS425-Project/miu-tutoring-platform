package edu.miu.cs.cs425.project.miututoring.api.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class Section {
    public enum BlockMonth {
        JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sectionId;

    private String sectionName;

    @NotBlank(message = "Class room is required")
    private String classRoom;

    //@NotBlank(Is not working for the Enums!!!)
    @NotNull
    private BlockMonth month;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "courseId")
    private Course course;                                                 //assuming the relation is one directional,

    @ManyToOne(cascade = CascadeType.ALL)                                   //(name ="faculty_by",nullable=false)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "section_id")
    private List<TutorialGroup> tutorialGroup;

    public Section() {
    }

    public Section(String sectionName, String classRoom, BlockMonth month, Course course, Faculty faculty
//            ,List<TutorialGroup> tutorialGroup                            //with this one it will result data persistence exception
    ) {
        this.sectionName = sectionName;
        this.classRoom = classRoom;
        this.month = month;
        this.course = course;
        this.faculty = faculty;
//        this.tutorialGroup = tutorialGroup;
    }

    public Section(String sectionName, String classRoom, BlockMonth month){
        this.sectionName = sectionName;
        this.classRoom = classRoom;
        this.month = month;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    public BlockMonth getMonth() {
        return month;
    }

    public void setMonth(BlockMonth month) {
        this.month = month;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public List<TutorialGroup> getTutorialGroup() {
        return tutorialGroup;
    }

    public void setTutorialGroup(List<TutorialGroup> tutorialGroup) {
        this.tutorialGroup = tutorialGroup;
    }

    @Override
    public String toString() {
        return "Section{" +
                "sectionName='" + sectionName + '\'' +
                ", classRoom='" + classRoom + '\'' +
                ", month=" + month +
                ", course=" + course +
                ", faculty=" + faculty +
                ", tutorialGroup=" + tutorialGroup +
                '}';
    }

}
