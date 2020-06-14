package edu.miu.cs.cs425.project.miututoring.api.service.impl;

import edu.miu.cs.cs425.project.miututoring.api.model.Enrollment;
import edu.miu.cs.cs425.project.miututoring.api.model.TutorRequest;
import edu.miu.cs.cs425.project.miututoring.api.model.TutorialGroup;
import edu.miu.cs.cs425.project.miututoring.api.repository.EnrollmentRepository;
import edu.miu.cs.cs425.project.miututoring.api.repository.TutorRequestRepository;
import edu.miu.cs.cs425.project.miututoring.api.repository.TutorialGroupRepository;
import edu.miu.cs.cs425.project.miututoring.api.service.TutorRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TutorRequestImpl implements TutorRequestService {

    TutorRequestRepository tutorRequestRepository;
    EnrollmentRepository enrollmentRepository;
    TutorialGroupRepository tutorialGroupRepository;

    @Autowired
    public TutorRequestImpl(TutorRequestRepository tutorRequestRepository, EnrollmentRepository enrollmentRepository, TutorialGroupRepository tutorialGroupRepository) {
        this.tutorRequestRepository = tutorRequestRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.tutorialGroupRepository = tutorialGroupRepository;
    }

    @Override
    public Page<TutorRequest> listTutorRequests(int pageNo, Integer pageSize, String sortBy, Boolean sortDesc) {
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
        return tutorRequestRepository.save(tutorRequest);
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
            tutorialGroup.setTutor(enrollment.getStudent());
            tutorialGroupRepository.save(tutorialGroup);
            enrollmentRepository.save(enrollment);
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
