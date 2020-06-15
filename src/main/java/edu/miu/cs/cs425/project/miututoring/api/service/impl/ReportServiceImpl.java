package edu.miu.cs.cs425.project.miututoring.api.service.impl;

import edu.miu.cs.cs425.project.miututoring.api.model.Course;
import edu.miu.cs.cs425.project.miututoring.api.model.Report;
import edu.miu.cs.cs425.project.miututoring.api.model.Student;
import edu.miu.cs.cs425.project.miututoring.api.model.TutorialGroup;
import edu.miu.cs.cs425.project.miututoring.api.repository.ReportRepository;
import edu.miu.cs.cs425.project.miututoring.api.service.ReportService;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    ReportRepository reportRepository;


    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public List<Report> getAllReports() {
        return (List<Report>) reportRepository.findAll();
    }

    @Override
    public Page<Report> getAllReportsPaged(int pageNo, Integer pageSize, String sortBy, Boolean sortDesc) {
        return reportRepository.findAll(PageRequest.of(pageNo, pageSize == -1 ? Integer.MAX_VALUE: pageSize, sortBy.equals("") ? Sort.unsorted() : Sort.by(sortDesc ? Sort.Direction.DESC :Sort.Direction.ASC ,sortBy)));
    }

    @Override
    public List<Report> getAllReportsInATutorialGroup(Long tutorialGroupId) {
        return reportRepository.findAllByTutorialGroup_TutorialGroupId(tutorialGroupId);
    }

    @Override
    public Report getReportById(Integer reportId) {
        return reportRepository.findById(reportId).orElse(null);
    }

    @Override
    public List<Report> getReportByStudentId(Long studentId) {
        return reportRepository.findAllByStudent_Id(studentId);
    }

    @Override
    public Report saveReport(Report report) {
        return reportRepository.save(report);
    }

    @Override
    public Report updateReport(Report updatedReport, Integer reportId) {
        return reportRepository.findById(reportId).map(report-> {
            report.setTutorialGroup(updatedReport.getTutorialGroup());
            report.setCourse(updatedReport.getCourse());
            report.setReport(updatedReport.getReport());
            report.setStudent(updatedReport.getStudent());
            return reportRepository.save(report);})
                .orElseGet(()-> reportRepository.save(updatedReport));
    }

    @Override
    public void deleteReportById(Integer reportId) {
        reportRepository.deleteById(reportId);
    }

    @Override
    public Page<Report> searchReports(String searchQuery, int pageNo, Integer pageSize, String sortBy, Boolean sortDesc) {
        return  reportRepository.findAllByReportTextContainingOrStudent_FirstNameContainingOrCourse_CourseNameContainingOrTutorialGroup_TutorialGroupNumberContainingOrderByReportText(searchQuery, searchQuery, searchQuery, searchQuery, PageRequest.of(pageNo, pageSize, sortBy.equals("") ? Sort.unsorted() : Sort.by(sortDesc ? Sort.Direction.DESC :Sort.Direction.ASC ,sortBy)));
    }
}
