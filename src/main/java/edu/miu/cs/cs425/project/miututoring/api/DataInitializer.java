package edu.miu.cs.cs425.project.miututoring.api;

import edu.miu.cs.cs425.project.miututoring.api.model.*;
import edu.miu.cs.cs425.project.miututoring.api.service.*;
import edu.miu.cs.cs425.project.miututoring.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    FacultyService facultyService;
    StudentService studentService;
    MyUserDetailsService userService;
    CourseService courseService;
    SectionService sectionService;
    TutorialGroupRepository tutorialGroupRepository;
    EnrollmentService enrollmentService;
    PasswordEncoder passwordEncoder;
    ReportService reportService;
    TutorRequestService tutorRequestService;

    @Autowired
    public DataInitializer(FacultyService facultyService, StudentService studentService,
                           MyUserDetailsService myUserDetailsService, CourseService courseService,
                           SectionService sectionService, TutorialGroupRepository tutorialGroupRepository,
                           @Lazy PasswordEncoder passwordEncoder, EnrollmentService enrollmentService, ReportService reportService, TutorRequestService tutorRequestService){
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.userService = myUserDetailsService;
        this.courseService = courseService;
        this.sectionService = sectionService;
        this.tutorialGroupRepository = tutorialGroupRepository;
        this.passwordEncoder = passwordEncoder;
        this.enrollmentService = enrollmentService;
        this.reportService = reportService;
        this.tutorRequestService = tutorRequestService;
    }

    @Override
    public void run(String... args) throws Exception {

        Boolean isInitialized = false;

        // Admin
        List<User> usersList = new ArrayList<User>(Arrays.asList(
                new User("admin@miu.edu","admin","George","","Cannon", new ArrayList<>(Arrays.asList("ROLE_ADMIN")))
        ));
        saveUsers(usersList);

        // Student
        Student student1 = new Student("student@miu.edu","student","000-61-0001", "Tesfaye", "Lemma", "Girum", 3.45, LocalDate.of(2019,5,24));
        Student student2 = new Student("tutor@miu.edu","tutor123","000-61-0002", "Haile", "Teshale", "Alemu", 3.24, LocalDate.of(2019,5,21));
        Student student3 = new Student("rmhbcd@hi2.in","student","000-61-0003", "Tigist", "Gutema", "Kefyalew", 3.84, LocalDate.of(2019,5,21));
        
        try {
            saveStudent(student1);
            saveStudent(student2);
            saveStudent(student3);
        }
        catch(Exception ex){
            student1 = studentService.getByUsername("student@miu.edu");
            System.out.println("Data already initialized");
            isInitialized = true;
        }

        if(!isInitialized){

            // Faculty
            Faculty faculty1 = new Faculty("faculty1@miu.edu","faculty", "John", "A", "Peterson","Software Engineeing CS425");
            Faculty faculty2 = new Faculty("faculty2@miu.edu","faculty", "Michael", "", "Zijlstra", "Modern Programming Practices CS401");
            try {
                saveFaculty(faculty1);
                saveFaculty(faculty2);
            }
            catch(Exception ex){
                faculty1 = facultyService.getByUsername("faculty@miu.edu");
            }

            // Course
            Course course1 = new Course("CS425", "Software Engineering", 4);
            Course course2 = new Course("CS401", "Modern Programming Practices", 4);

            saveCourse(course1);
            saveCourse(course2);

            // Section
            Section section1 = new Section("CS425-2020-06-01", "Library 109", "2020-06", course1, faculty1);
            Section section2 = new Section("CS420-2020-06-01", "Library 110", "2020-06", course2, faculty2);
            saveSection(section1);
            saveSection(section2);

            // Tutorial Group
            TutorialGroup tutorialGroup1 = new TutorialGroup("Group 1",section1);
            TutorialGroup tutorialGroup2 = new TutorialGroup("Group 2",section1);
            TutorialGroup tutorialGroup3 = new TutorialGroup("Group 1", section2);
            TutorialGroup tutorialGroup4 = new TutorialGroup("Group 2", section2);
            saveTutorialGroup(tutorialGroup1);
            saveTutorialGroup(tutorialGroup2);
            saveTutorialGroup(tutorialGroup3);
            saveTutorialGroup(tutorialGroup4);
            

            // Enrollment
            Enrollment enrollment1 = new Enrollment(student1, section1);
            Enrollment enrollment2 = new Enrollment(student2, section2);
            enrollment1.setTutorialGroup(tutorialGroup1);
            enrollment2.setTutorialGroup(tutorialGroup3);
            saveEnrollment(enrollment1);
            saveEnrollment(enrollment2);
    
            List<Enrollment> list = new ArrayList<>();
            list.add(enrollment1);
            tutorialGroup1.setEnrollments(list);
            saveTutorialGroup(tutorialGroup1);
    
            List<Enrollment> list1 = new ArrayList<>();
            list1.add(enrollment2);
            tutorialGroup3.setEnrollments(list1);
            saveTutorialGroup(tutorialGroup3);
    
            //Tutor Request
            TutorRequest request1 = new TutorRequest(section1, enrollment1, "I have 4 years experience teaching students");
            saveTutorialRequest(request1);
            TutorRequest request2 = new TutorRequest(section2,enrollment2, "I have 2 years experience in the tech industry");
            saveTutorialRequest(request2);

            enrollment1.setTutorRequest(request1);
            enrollment2.setTutorRequest(request2);

            saveEnrollment(enrollment1);
            saveEnrollment(enrollment2);
    
            //Report
            Report report1 = new Report(student1, tutorialGroup1, course1, "This group was really helping, I understood a lot about the course");
            saveReport(report1);
            Report report2 = new Report(student3, tutorialGroup3, course2, "The tutor is not willing to answer some of our questions");
            saveReport(report2);

            System.out.println("Successfully Initialized");
        }
    }

    public void saveStudent(Student student) throws Exception {
        studentService.registerStudent(student);
    }

    public void saveFaculty(Faculty faculty) throws Exception {
        facultyService.registerFaculty(faculty);
    }

    public void saveUsers(List<User> users) {
        userService.saveUsers(users);
    }

    public void saveCourse(Course course) {
        courseService.saveCourse(course);
    }

    public void saveSection(Section section) {
        sectionService.registerSection(section);
    }

    public void saveTutorialGroup(TutorialGroup tutorialGroup) {
        tutorialGroupRepository.save(tutorialGroup);
    }

    public void saveEnrollment(Enrollment enrollment) {
        enrollmentService.saveEnrollment(enrollment);
    }

    public void saveReport(Report report) {
        reportService.saveReport(report);
    }

    public void saveTutorialRequest(TutorRequest tutorRequest) {
        tutorRequestService.saveTutorRequest(tutorRequest);
    }

}
