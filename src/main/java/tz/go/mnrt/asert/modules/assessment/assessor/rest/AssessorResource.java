package tz.go.mnrt.asert.modules.assessment.assessor.rest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.ValidationException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tz.go.mnrt.asert.configs.NoAuthorization;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorApprovalDto;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorCertificateDto;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorDto;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorProfileDto;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorRejectDto;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.UploadPhotoDto;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.AssessmentStatus;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.AssessorHotel;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.AssessorStatus;
import tz.go.mnrt.asert.modules.assessment.assessor.repository.AssessorHotelRepository;
import tz.go.mnrt.asert.modules.assessment.assessor.repository.AssessorRepository;
import tz.go.mnrt.asert.modules.assessment.assessor.service.AssessorCertificateService;
import tz.go.mnrt.asert.modules.assessment.assessor.service.AssessorService;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelResponseDto;

@RestController
@RequestMapping(Constant.API_V1 + "/assessors")
@RequiredArgsConstructor
public class AssessorResource {

    private final AssessorService assessorService;
    private final AssessorRepository assessorRepository;
    private final AssessorHotelRepository assessorHotelRepository;
    private final AssessorCertificateService certificateService;

    @GetMapping
    public ResponseEntity<CustomApiResponse> get(
            Pageable pagination,
            @RequestParam Map<String, String> search) {

        CustomApiResponse response = CustomApiResponse.ok(
                assessorService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("firstName").ascending())),
                        search));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/new-applications")
    public ResponseEntity<CustomApiResponse> newApplications(
            Pageable pagination,
            @RequestParam Map<String, String> search) {

        CustomApiResponse response = CustomApiResponse.ok(
                assessorService.newApplications(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("dateApplied").ascending())),
                        search));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rejected-applications")
    public ResponseEntity<CustomApiResponse> rejectedApplications(
            Pageable pagination,
            @RequestParam Map<String, String> search) {

        CustomApiResponse response = CustomApiResponse.ok(
                assessorService.rejectedApplications(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("dateApplied").ascending())),
                        search));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/approved-applications")
    public ResponseEntity<CustomApiResponse> approvedApplications(
            Pageable pagination,
            @RequestParam Map<String, String> search) {

        CustomApiResponse response = CustomApiResponse.ok(
                assessorService.approvedApplications(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("dateVerified").ascending())),
                        search));
        return ResponseEntity.ok(response);
    }

    @NoAuthorization
    @GetMapping("/portal-approved-applications")
    public ResponseEntity<CustomApiResponse> portalApprovedApplications(
            Pageable pagination,
            @RequestParam Map<String, String> search) {

        CustomApiResponse response = CustomApiResponse.ok(
                assessorService.approvedApplications(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("dateVerified").ascending())),
                        search));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{uuid}/profile")
    public ResponseEntity<CustomApiResponse> profile(@PathVariable("uuid") UUID uuid) {
        Assessor assessor = assessorRepository.findByUuid(uuid)
                .orElseThrow(() -> new ValidationException("Assessor not found"));
        AssessorProfileDto profileDto = new AssessorProfileDto(assessor);
        CustomApiResponse response = CustomApiResponse.ok(profileDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<CustomApiResponse> create(@Valid @RequestBody AssessorDto assessorDto) {
        if (assessorDto.getId() != null || assessorDto.getUuid() != null) {
            throw new ValidationException("New Assessor cannot contain id or uuid");
        }
        CustomApiResponse response = CustomApiResponse.created(
                "Assessor created successfully",
                assessorService.save(assessorDto));
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> update(
            @Valid @RequestBody AssessorDto assessorDto,
            @PathVariable UUID uuid) {

        assessorDto.setUuid(uuid);

        if (assessorDto.getUuid() == null || !Objects.equals(assessorDto.getUuid(), uuid)) {
            throw new ValidationException(
                    "Assessor id must be present and equals to path id {" + uuid + "}");
        }

        // Check if assessor can be updated based on current status
        Optional<Assessor> existingAssessor = assessorRepository.findByUuid(uuid);
        if (existingAssessor.isPresent()) {
            AssessorStatus currentStatus = existingAssessor.get().getStatus();
            if (currentStatus == AssessorStatus.PENDING) {
                throw new ValidationException("Cannot update assessor profile while application is under review");
            }
            if (currentStatus == AssessorStatus.APPROVED) {
                throw new ValidationException("Cannot update approved assessor profile");
            }
        }

        CustomApiResponse response = CustomApiResponse.ok(
                "Assessor updated successfully",
                assessorService.save(assessorDto));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CustomApiResponse> findById(@PathVariable UUID uuid) {
        CustomApiResponse response = CustomApiResponse.ok(
                assessorService.findByUuid(uuid));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/current-user")
    public ResponseEntity<CustomApiResponse> getByCurrentUser() {
        AssessorDto assessorDto = assessorService.getCurrentAssessor();
        if (assessorDto == null) {
            CustomApiResponse response = CustomApiResponse.notFound("Assessor not found");
            return ResponseEntity.ok(response);
        }

        Assessor assessor = assessorRepository.findById(assessorDto.getId())
                .orElseThrow(() -> new ValidationException("Assessor not found"));
        AssessorProfileDto profileDto = new AssessorProfileDto(assessor);
        CustomApiResponse response = CustomApiResponse.ok(profileDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/profile-completion-percentage")
    public ResponseEntity<CustomApiResponse> profileCompletionPercentage(@PathVariable("id") Long id) {
        try {
            AssessorDto assessor = assessorService.getCurrentAssessor();
            if (id > 0) {
                assessor = assessorService.getById(id);
            }
            assessor.setCompletionPercentage(assessorService.calculateProfileCompletion(assessor.getId()));
            CustomApiResponse response = CustomApiResponse.ok(assessor);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CustomApiResponse response = CustomApiResponse.notFound("Assessor not found");
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> delete(@PathVariable UUID uuid) {
        assessorService.delete(uuid);
        CustomApiResponse response = CustomApiResponse.noContent("Assessor deleted successfully");
        return ResponseEntity.status(204).body(response);
    }

    @PostMapping("/upload-photo")
    public ResponseEntity<CustomApiResponse> uploadPhoto(@Valid @RequestBody UploadPhotoDto assessorDto) {
        Optional<Assessor> row = assessorService.findById(assessorDto.getId());
        if (row.isEmpty()) {
            throw new ValidationException("Assessor not found");
        } else {
            Assessor assessor = row.get();
            String photo = assessorService.uploadPhoto(assessorDto.getPhoto());
            assessor.setPhoto(photo);
            assessorRepository.save(assessor);
            CustomApiResponse response = CustomApiResponse.accepted(
                    "Assessor Photo Uploaded successfully",
                    assessorDto);
            return ResponseEntity.status(201).body(response);
        }
    }

    @PostMapping("/set-profile-photo")
    public ResponseEntity<CustomApiResponse> setProfilePhoto(@RequestBody Map<String, Object> requestBody) {
        Long assessorId = Long.valueOf(requestBody.get("assessorId").toString());
        Long fileUploadId = requestBody.get("fileUploadId") != null
                ? Long.valueOf(requestBody.get("fileUploadId").toString())
                : null;

        assessorService.setProfilePhoto(assessorId, fileUploadId);

        CustomApiResponse response = CustomApiResponse.ok(
                "Profile photo updated successfully",
                null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit-application")
    @Transactional
    public ResponseEntity<CustomApiResponse> submitApplication() {
        AssessorDto currentAssessor = assessorService.getCurrentAssessor();

        Optional<Assessor> row = assessorService.findById(currentAssessor.getId());
        if (row.isEmpty()) {
            throw new ValidationException("Assessor not found");
        }

        Assessor assessor = row.get();

        // Validate current status - can only submit if DRAFT or REJECTED
        if (assessor.getStatus() != AssessorStatus.DRAFT && assessor.getStatus() != AssessorStatus.REJECTED) {
            throw new ValidationException("Application cannot be submitted. Current status: " + assessor.getStatus());
        }

        // Update status to PENDING and set submission timestamp
        assessor.setStatus(AssessorStatus.PENDING);
        assessor.setDateApplied(LocalDateTime.now());
        assessor.setDateRejected(null);
        assessor.setDateVerified(null);
        assessor.setVerificationNotes(null);
        assessor.setRejectionReason(null);
        assessor.setReasonId(null);

        assessorRepository.save(assessor);

        CustomApiResponse response = CustomApiResponse.ok(
                "Application submitted for review successfully",
                new AssessorProfileDto(assessor));
        return ResponseEntity.ok(response);
    }

    @Transactional
    @PostMapping("/reject")
    public CustomApiResponse reject(@Valid @RequestBody AssessorRejectDto rejectDto) {
        Optional<Assessor> row = assessorService.findById(rejectDto.getId());
        if (row.isEmpty()) {
            throw new ValidationException("Assessor not found");
        }

        Assessor assessor = row.get();

        // Validate current status - can only reject if PENDING
        if (assessor.getStatus() != AssessorStatus.PENDING) {
            throw new ValidationException("Application cannot be rejected. Current status: " + assessor.getStatus());
        }

        assessor.setRejectionReason(rejectDto.getRejectionReason());
        assessor.setReasonId(rejectDto.getReasonId());
        assessor.setDateRejected(LocalDateTime.now());
        assessor.setStatus(AssessorStatus.REJECTED);
        assessor.setDateVerified(null);
        assessorRepository.save(assessor);
        return CustomApiResponse.ok(
                "Assessor Rejected Successfully",
                new AssessorProfileDto(assessor));
    }

    @Transactional
    @PostMapping("/approve")
    public CustomApiResponse approve(@Valid @RequestBody AssessorApprovalDto assessorApprovalDto) {
        Optional<Assessor> row = assessorService.findById(assessorApprovalDto.getId());
        if (row.isEmpty()) {
            throw new ValidationException("Assessor not found");
        }

        Assessor assessor = row.get();

        // Validate current status - can only approve if PENDING
        if (assessor.getStatus() != AssessorStatus.PENDING) {
            throw new ValidationException("Application cannot be approved. Current status: " + assessor.getStatus());
        }

        assessor.setVerificationNotes(assessorApprovalDto.getVerificationNotes());
        assessor.setStatus(AssessorStatus.APPROVED);
        assessor.setDateVerified(LocalDateTime.now());
        assessorRepository.save(assessor);
        return CustomApiResponse.ok(
                "Assessor Approved Successfully",
                new AssessorProfileDto(assessor));
    }

    @GetMapping("/my-assigned-hotels")
    public ResponseEntity<CustomApiResponse> myAssignedHotels(Pageable pagination,
            @RequestParam Map<String, String> search) {
        AssessorDto currentAssessor = assessorService.getCurrentAssessor();
        if (currentAssessor == null) {
            throw new ValidationException("Assessor not found");
        } else {
            search.put("assessorId", currentAssessor.getId().toString());
            Page<HotelResponseDto> items = assessorService.assessmentAssignments(search, pagination);
            CustomApiResponse response = CustomApiResponse.ok(items);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/my-new-assignments")
    public ResponseEntity<CustomApiResponse> myNewAssignments(Pageable pagination,
            @RequestParam Map<String, String> search) {
        AssessorDto currentAssessor = assessorService.getCurrentAssessor();
        if (currentAssessor == null) {
            throw new ValidationException("Assessor not found");
        } else {
            search.put("assessorId", currentAssessor.getId().toString());
            Page<HotelResponseDto> items = assessorService.newAssignments(search, pagination);
            CustomApiResponse response = CustomApiResponse.ok(items);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/my-assignments-awaiting-approval")
    public ResponseEntity<CustomApiResponse> myAssignmentsAwaitingApproval(Pageable pagination,
            @RequestParam Map<String, String> search) {
        AssessorDto currentAssessor = assessorService.getCurrentAssessor();
        if (currentAssessor == null) {
            throw new ValidationException("Assessor not found");
        } else {
            search.put("assessorId", currentAssessor.getId().toString());
            Page<HotelResponseDto> items = assessorService.submittedAssignments(search, pagination);
            CustomApiResponse response = CustomApiResponse.ok(items);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/assignments-awaiting-approval")
    public ResponseEntity<CustomApiResponse> assignmentsAwaitingApproval(Pageable pagination,
            @RequestParam Map<String, String> search) {
        Page<HotelResponseDto> items = assessorService.submittedAssignments(search, pagination);
        CustomApiResponse response = CustomApiResponse.ok(items);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/completed-assignments")
    public ResponseEntity<CustomApiResponse> completedAssignments(Pageable pagination,
            @RequestParam Map<String, String> search) {
        Page<HotelResponseDto> items = assessorService.completedAssignments(search, pagination);
        CustomApiResponse response = CustomApiResponse.ok(items);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-completed-assignments")
    public ResponseEntity<CustomApiResponse> myCompletedAssignments(Pageable pagination,
            @RequestParam Map<String, String> search) {
        AssessorDto currentAssessor = assessorService.getCurrentAssessor();
        if (currentAssessor == null) {
            throw new ValidationException("Assessor not found");
        } else {
            search.put("assessorId", currentAssessor.getId().toString());
            Page<HotelResponseDto> items = assessorService.completedAssignments(search, pagination);
            CustomApiResponse response = CustomApiResponse.ok(items);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/assessment-assignments")
    public ResponseEntity<CustomApiResponse> hotelAssessmentAssignments(Pageable pagination,
            @RequestParam Map<String, String> search) {
        AssessorDto currentAssessor = assessorService.getCurrentAssessor();
        if (currentAssessor == null) {
            throw new ValidationException("Assessor not found");
        } else {
            Page<HotelResponseDto> items = assessorService.assessmentAssignments(search, pagination);
            CustomApiResponse response = CustomApiResponse.ok(items);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/get-assigned-listings/{uuid}")
    public ResponseEntity<CustomApiResponse> getAssignedListings(Pageable pagination, @PathVariable("uuid") UUID uuid) {
        AssessorDto currentAssessor = assessorService.findByUuid(uuid);
        if (currentAssessor == null) {
            throw new ValidationException("Assessor not found");
        } else {
            Map<String, String> search = new HashMap<>();
            search.put("assessorId", currentAssessor.getId().toString());
            Page<HotelResponseDto> items = assessorService.newAssignments(search, pagination);
            CustomApiResponse response = CustomApiResponse.ok(items);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/{id}/submit-assessment-data")
    public ResponseEntity<CustomApiResponse> submitAssessmentData(@PathVariable("id") Long id) {
        AssessorHotel assessorHotel = assessorHotelRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Assignment not found"));
        assessorHotel.setDataCollected(true);
        assessorHotelRepository.save(assessorHotel);
        CustomApiResponse response = CustomApiResponse.ok("Assessment Data Submitted Successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/approve-assessment-data")
    public ResponseEntity<CustomApiResponse> approveAssessmentData(@PathVariable("id") Long id) {
        AssessorHotel assessorHotel = assessorHotelRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Assignment not found"));
        assessorHotel.setDataCollected(true);
        assessorHotel.setStatus(AssessmentStatus.Completed);
        assessorHotelRepository.save(assessorHotel);
        CustomApiResponse response = CustomApiResponse.ok("Assessment Data Approved Successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/certificates")
    @Transactional
    public ResponseEntity<CustomApiResponse> createCertificate(
            @Valid @RequestBody AssessorCertificateDto certificateDto) {
        AssessorCertificateDto createdCertificate = certificateService.save(certificateDto);
        CustomApiResponse response = CustomApiResponse.created("Certificate created successfully", createdCertificate);
        return ResponseEntity.status(201).body(response);
    }

}
