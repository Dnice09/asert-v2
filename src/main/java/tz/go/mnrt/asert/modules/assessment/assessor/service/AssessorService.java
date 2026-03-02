package tz.go.mnrt.asert.modules.assessment.assessor.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorDto;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorHotelDto;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.assessment.certification.dto.AssessorCertificationDto;
import tz.go.mnrt.asert.modules.assessment.document.dto.AssessorDocumentDto;
import tz.go.mnrt.asert.modules.assessment.educationbackground.dto.EducationBackgroundDto;
import tz.go.mnrt.asert.modules.assessment.employmenthistory.dto.EmploymentHistoryDto;
import tz.go.mnrt.asert.modules.assessment.reference.dto.AssessorReferenceDto;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelResponseDto;

@Service
public interface AssessorService {
    AssessorDto save(AssessorDto assessorDto);

    Page<AssessorDto> findAll(Pageable page, Map<String, String> search);

    Page<AssessorDto> newApplications(Pageable page, Map<String, String> search);

    Page<AssessorDto> approvedApplications(Pageable page, Map<String, String> search);

    Page<AssessorDto> rejectedApplications(Pageable page, Map<String, String> search);

    AssessorDto findByUuid(UUID id);

    AssessorDto getById(Long id);

    Optional<Assessor> findById(Long id);

    void delete(UUID uuid);

    AssessorDto getCurrentAssessor();

    int calculateProfileCompletion(Long assessorId);

    List<EducationBackgroundDto> educationBackground(Long assessorId);

    List<EmploymentHistoryDto> employmentHistory(Long assessorId);

    List<AssessorReferenceDto> references(Long assessorId);

    List<AssessorDocumentDto> documents(Long assessorId);

    List<AssessorCertificationDto> certifications(Long assessorId);

    String uploadPhoto(String base64String);

    void setProfilePhoto(Long assessorId, Long fileUploadId);

    void assignHotels(Long assessorId, AssessorHotelDto dto);

    Page<HotelResponseDto> assessmentAssignments(Map<String, String> search, Pageable page);

    Page<HotelResponseDto> newAssignments(Map<String, String> search, Pageable page);

    Page<HotelResponseDto> submittedAssignments(Map<String, String> search, Pageable page);

    Page<HotelResponseDto> completedAssignments(Map<String, String> search, Pageable page);
}
