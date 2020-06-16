package edu.miu.cs.cs425.project.miututoring.api.service.impl;

import edu.miu.cs.cs425.project.miututoring.api.model.Enrollment;
import edu.miu.cs.cs425.project.miututoring.api.model.TutorRequest;
import edu.miu.cs.cs425.project.miututoring.api.model.TutorialGroup;
import edu.miu.cs.cs425.project.miututoring.api.repository.EnrollmentRepository;
import edu.miu.cs.cs425.project.miututoring.api.repository.TutorRequestRepository;
import edu.miu.cs.cs425.project.miututoring.api.repository.TutorialGroupRepository;
import edu.miu.cs.cs425.project.miututoring.api.service.NotificationService;
import edu.miu.cs.cs425.project.miututoring.api.service.TutorRequestService;
import edu.miu.cs.cs425.project.miututoring.api.util.EmailGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.List;

@Service
public class TutorRequestImpl implements TutorRequestService {

    @Value("${spring.mail.username}")
    private String username;

    TutorRequestRepository tutorRequestRepository;
    EnrollmentRepository enrollmentRepository;
    TutorialGroupRepository tutorialGroupRepository;
    NotificationService notificationService;

    @Autowired
    public TutorRequestImpl(TutorRequestRepository tutorRequestRepository, EnrollmentRepository enrollmentRepository, TutorialGroupRepository tutorialGroupRepository, NotificationService notificationService) {
        this.tutorRequestRepository = tutorRequestRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.tutorialGroupRepository = tutorialGroupRepository;
        this.notificationService = notificationService;
    }

    @Override
    public List<TutorRequest> listTutorRequests() {
        return tutorRequestRepository.findAll();
    }

    @Override
    public Page<TutorRequest> listTutorRequestsPaged(int pageNo, Integer pageSize, String sortBy, Boolean sortDesc) {
        return tutorRequestRepository.findAll(PageRequest.of(pageNo, pageSize == -1 ? Integer.MAX_VALUE :pageSize, sortBy.equals("") ? Sort.unsorted() : Sort.by(sortDesc ? Sort.Direction.DESC :Sort.Direction.ASC ,sortBy)));
    }


    @Override
    public TutorRequest getTutorRequestById(Integer tutorRequestId) {
        return tutorRequestRepository.findById(tutorRequestId).orElse(null);
    }

    @Override
    public void deleteTutorRequestById(Integer tutorRequestId) {
        tutorRequestRepository.deleteById(tutorRequestId);
    }

    @Override
    public TutorRequest saveTutorRequest(TutorRequest tutorRequest) {
        Enrollment enrollment = tutorRequest.getEnrollment();
        TutorRequest savedTutorRequest = tutorRequestRepository.save(tutorRequest);
        enrollment.setTutorRequest(savedTutorRequest);
        enrollmentRepository.save(enrollment);
        return savedTutorRequest;
    }

    @Override
    public TutorRequest updateTutorRequest(TutorRequest updatedTutorRequest, Integer tutorRequestId) {
        return tutorRequestRepository.findById(tutorRequestId).map(tutorRequest -> {
            tutorRequest.setSection(updatedTutorRequest.getSection());
            tutorRequest.setStatus(updatedTutorRequest.getStatus());
            tutorRequest.setEnrollment(updatedTutorRequest.getEnrollment());
            return tutorRequestRepository.save(tutorRequest);
        }).orElseGet(()-> tutorRequestRepository.save(updatedTutorRequest));
    }

    @Override
    public TutorRequest acceptTutorRequest(Integer tutorRequestId, TutorialGroup tutorialGroup) {
        return tutorRequestRepository.findById(tutorRequestId).map(tutorRequest -> {
            tutorRequest.setStatus(TutorRequest.Status.ACCEPTED);
            TutorRequest updatedRequest =  tutorRequestRepository.save(tutorRequest);
            Enrollment enrollment = enrollmentRepository.findById(updatedRequest.getEnrollment().getEnrollmentId()).get();
            enrollment.setRole(Enrollment.RoleType.TUTOR);
            enrollment.setTutorialGroup(tutorialGroup);
            tutorialGroup.setTutor(enrollment.getStudent());
            tutorialGroupRepository.save(tutorialGroup);
            enrollmentRepository.save(enrollment);
            try {
                String message = EmailGenerator.requestAccepted(tutorRequest.getEnrollment().getStudent().getFirstName(),
                        tutorRequest.getSection().getCourse().getCourseName(),tutorialGroup.getTutorialGroupNumber());
                String body = EmailGenerator.generateEmail(message);
                notificationService.sendNotification(username,tutorRequest.getEnrollment().getStudent().getUsername(),
                        body, "Tutor request accepted");
            } catch (MessagingException e) {
                System.out.println("Unable to send email");
            }
            return updatedRequest;
        }).orElse(null);
    }

    @Override
    public TutorRequest denyTutorRequest(Integer tutorRequestId) {
        return tutorRequestRepository.findById(tutorRequestId).map(tutorRequest -> {
            tutorRequest.setStatus(TutorRequest.Status.REJECTED);
            return tutorRequestRepository.save(tutorRequest);
        }).orElse(null);
    }

    @Override
    public Page<TutorRequest> getTutorRequestByStudent(Long studentId, Integer page, Integer itemsPerPage, String sortBy, Boolean sortDesc) {
        return tutorRequestRepository.findAllByEnrollment_Student_IdEquals(studentId, PageRequest.of(page, itemsPerPage == -1 ? Integer.MAX_VALUE :itemsPerPage, sortBy.equals("") ? Sort.unsorted() : Sort.by(sortDesc ? Sort.Direction.DESC :Sort.Direction.ASC ,sortBy)));
    }
}
