package tz.go.mnrt.asert.modules.assessment.assessor.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorDto;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorHotelDto;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.AssessmentStatus;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.AssessorHotel;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.AssessorStatus;
import tz.go.mnrt.asert.modules.assessment.assessor.repository.AssessorHotelRepository;
import tz.go.mnrt.asert.modules.assessment.assessor.repository.AssessorHotelSpecification;
import tz.go.mnrt.asert.modules.assessment.assessor.repository.AssessorRepository;
import tz.go.mnrt.asert.modules.assessment.assessor.repository.AssessorSpecification;
import tz.go.mnrt.asert.modules.assessment.assessor.service.AssessorService;
import tz.go.mnrt.asert.modules.assessment.certification.dto.AssessorCertificationDto;
import tz.go.mnrt.asert.modules.assessment.certification.entity.AssessorCertification;
import tz.go.mnrt.asert.modules.assessment.certification.repository.AssessorCertificationRepository;
import tz.go.mnrt.asert.modules.assessment.certification.repository.AssessorCertificationSpecification;
import tz.go.mnrt.asert.modules.assessment.document.dto.AssessorDocumentDto;
import tz.go.mnrt.asert.modules.assessment.document.entity.AssessorDocument;
import tz.go.mnrt.asert.modules.assessment.document.repository.AssessorDocumentRepository;
import tz.go.mnrt.asert.modules.assessment.document.repository.AssessorDocumentSpecification;
import tz.go.mnrt.asert.modules.assessment.educationbackground.dto.EducationBackgroundDto;
import tz.go.mnrt.asert.modules.assessment.educationbackground.entity.EducationBackground;
import tz.go.mnrt.asert.modules.assessment.educationbackground.repository.EducationBackgroundRepository;
import tz.go.mnrt.asert.modules.assessment.educationbackground.repository.EducationBackgroundSpecification;
import tz.go.mnrt.asert.modules.assessment.employmenthistory.dto.EmploymentHistoryDto;
import tz.go.mnrt.asert.modules.assessment.employmenthistory.entity.EmploymentHistory;
import tz.go.mnrt.asert.modules.assessment.employmenthistory.repository.EmploymentHistoryRepository;
import tz.go.mnrt.asert.modules.assessment.employmenthistory.repository.EmploymentHistorySpecification;
import tz.go.mnrt.asert.modules.assessment.reference.dto.AssessorReferenceDto;
import tz.go.mnrt.asert.modules.assessment.reference.entity.AssessorReference;
import tz.go.mnrt.asert.modules.assessment.reference.repository.AssessorReferenceRepository;
import tz.go.mnrt.asert.modules.assessment.reference.repository.AssessorReferenceSpecification;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelResponseDto;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;
import tz.go.mnrt.asert.modules.setup.assessorrejectionreason.repository.AssessorRejectionReasonRepository;
import tz.go.mnrt.asert.modules.user.dtos.LoggedInUserDto;
import tz.go.mnrt.asert.modules.user.entity.User;
import tz.go.mnrt.asert.modules.user.repository.UserRepository;
import tz.go.mnrt.asert.modules.user.service.UserService;
import tz.go.mnrt.asert.modules.setup.fileupload.entity.FileUpload;
import tz.go.mnrt.asert.modules.setup.fileupload.repository.FileUploadRepository;

import javax.validation.ValidationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssessorServiceImpl extends SimpleSearchService<Assessor> implements AssessorService {
    private final AssessorRepository assessorRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AssessorRejectionReasonRepository assessorRejectionReasonRepository;
    private final EducationBackgroundRepository educationBackgroundRepository;
    private final EmploymentHistoryRepository employmentHistoryRepository;
    private final AssessorReferenceRepository assessorReferenceRepository;
    private final AssessorDocumentRepository assessorDocumentRepository;
    private final AssessorCertificationRepository assessorCertificationRepository;
    private final AssessorHotelRepository assessorHotelRepository;
    private final FileUploadRepository fileUploadRepository;

    @Value("${asert.uploaded-files.url}")
    private String uploadedFilesUrl;

    @Override
    public AssessorDto save(AssessorDto dto) {
        Assessor entity;

        if (dto.getUuid() != null) {
            // Update mode
            entity = assessorRepository.findByUuid(dto.getUuid())
                    .orElseThrow(() -> new ValidationException("Assessor not found"));
        } else {
            // Create mode
            entity = new Assessor();
            entity.setUuid(UUID.randomUUID());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setDeleted(false);
        }

        // Copy common fields only if they are provided in the DTO
        if (dto.getFirstName() != null)
            entity.setFirstName(dto.getFirstName());
        if (dto.getMiddleName() != null)
            entity.setMiddleName(dto.getMiddleName());
        if (dto.getLastName() != null)
            entity.setLastName(dto.getLastName());
        if (dto.getTitle() != null)
            entity.setTitle(dto.getTitle());
        if (dto.getEmail() != null)
            entity.setEmail(dto.getEmail());
        if (dto.getPhone() != null)
            entity.setPhone(dto.getPhone());
        if (dto.getPhoneTwo() != null)
            entity.setPhoneTwo(dto.getPhoneTwo());
        if (dto.getPhoto() != null)
            entity.setPhoto((dto.getPhoto()));
        if (dto.getDob() != null)
            entity.setDob(dto.getDob());
        if (dto.getSex() != null)
            entity.setSex(dto.getSex());
        if (dto.getIdentificationId() != null)
            entity.setIdentificationId(dto.getIdentificationId());
        if (dto.getIdentificationType() != null)
            entity.setIdentificationType(dto.getIdentificationType());
        if (dto.getStatus() != null)
            entity.setStatus(dto.getStatus());

        // Only update locationId if it's provided and not null/zero
        if (dto.getLocationId() != null && dto.getLocationId() > 0) {
            entity.setLocationId(dto.getLocationId());
        }

        // Set default status for new entities
        if (dto.getUuid() == null) {
            entity.setStatus(AssessorStatus.DRAFT);
            entity.setDateApplied(null);
        }

        if (dto.getReasonId() != null) {
            assessorRejectionReasonRepository.findById(dto.getReasonId()).ifPresent(reason -> {
                entity.setReasonId(reason.getId());
            });
        } else {
            entity.setReasonId(null);
        }

        entity.setVerificationNotes(null);
        entity.setVerifiedBy(null);
        entity.setDateVerified(null);

        LoggedInUserDto logged = userService.loggedIn()
                .orElseThrow(() -> new ValidationException("Logged in User not found"));

        User user = userRepository.findByUuid(logged.getUuid())
                .orElseThrow(() -> new ValidationException("User with uuid {" + logged.getUuid() + "} not found"));

        entity.setUserId(user.getId());
        entity.setUser(user);

        entity.setUpdatedAt(LocalDateTime.now());

        Assessor saved = assessorRepository.save(entity);

        return new AssessorDto(saved, uploadedFilesUrl);
    }

    @Override
    public AssessorDto getCurrentAssessor() {
        LoggedInUserDto logged = userService.loggedIn()
                .orElseThrow(() -> new ValidationException("Logged in User not found"));

        User user = userRepository.findByUuid(logged.getUuid())
                .orElseThrow(() -> new ValidationException("User with uuid {" + logged.getUuid() + "} not found"));

        Optional<Assessor> row = assessorRepository.findByUserId(user.getId());

        log.info("==========Found current logged in assessor with userId: {}=============", user.getId());

        return row.map(r -> new AssessorDto(r, uploadedFilesUrl)).orElse(null);
    }

    @Override
    public String uploadPhoto(String base64Photo) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Photo);

            String fileName = UUID.randomUUID().toString().replace("-", "") + ".jpg";
            Path uploadPath = Paths.get(uploadedFilesUrl);

            // Ensure /uploads folder exists
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, decodedBytes);

            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to save photo", e);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid base64 image data");
        }
    }

    @Override
    public Page<AssessorDto> findAll(Pageable page, Map<String, String> search) {
        log.info("finding paginated assossors with page {}  with search term {} ", page, search);
        Map<String, String> processedSearch = new HashMap<>(search);

        String status = processedSearch.remove("status");

        Specification<Assessor> specs = createSpecification(Assessor.class, processedSearch);

        if (status != null) {
            try {
                AssessorStatus approvalStatus = AssessorStatus.valueOf(status);
                log.info("Filtering assessors by status: {}", approvalStatus);
                specs = specs.and((root, query, cb) -> cb.equal(root.get("status"), approvalStatus));
            } catch (IllegalArgumentException e) {
                // Skip this filter if invalid value provided
                log.warn("Invalid status value: {}", status);
            }
        }

        return assessorRepository.findAll(specs, page).map(assessor -> new AssessorDto(assessor, uploadedFilesUrl));
    }

    @Override
    public Page<AssessorDto> newApplications(Pageable page, Map<String, String> search) {
        String filter = "";
        if (search != null && !search.isEmpty()) {
            filter = search.get("search");
        }

        Specification<Assessor> spec = Specification.where(AssessorSpecification.newApplication()
                .and(AssessorSpecification.notDeleted()).and(AssessorSpecification.byStatus(AssessorStatus.PENDING)));

        if (filter != null && !filter.isBlank()) {
            spec = spec.and(AssessorSpecification.search(filter));
        }

        Page<Assessor> results = assessorRepository.findAll(spec, page);

        return results.map(r -> new AssessorDto(r, uploadedFilesUrl));
    }

    @Override
    public Page<AssessorDto> rejectedApplications(Pageable page, Map<String, String> search) {
        String filter = "";
        if (search != null && !search.isEmpty()) {
            filter = search.get("search");
        }

        Specification<Assessor> spec = Specification.where(AssessorSpecification.rejectedApplication()
                .and(AssessorSpecification.notDeleted()).and(AssessorSpecification.byStatus(AssessorStatus.REJECTED)));

        if (filter != null && !filter.isBlank()) {
            spec = spec.and(AssessorSpecification.search(filter));
        }

        Page<Assessor> results = assessorRepository.findAll(spec, page);

        return results.map(r -> new AssessorDto(r, uploadedFilesUrl));
    }

    @Override
    public Page<AssessorDto> approvedApplications(Pageable page, Map<String, String> search) {
        String filter = "";
        long locationId = 0;
        String propertyType = "ALL";
        if (search != null && !search.isEmpty()) {
            filter = search.get("search");
            locationId = Long.parseLong(search.get("locationId") != null ? search.get("locationId") : "0");
            propertyType = search.get("preference") != null ? search.get("preference") : "ALL";
        }

        Specification<Assessor> spec = Specification.where(AssessorSpecification.approvedApplication()
                .and(AssessorSpecification.notDeleted()).and(AssessorSpecification.byStatus(AssessorStatus.APPROVED)));

        if (filter != null && !filter.isBlank()) {
            spec = spec.and(AssessorSpecification.search(filter));
        }

        if (locationId > 0) {
            spec = spec.and(AssessorSpecification.byLocation(locationId));
        }
        if (!propertyType.equals("ALL")) {
            PropertyType preference = PropertyType.valueOf(propertyType);
            spec = spec.and(AssessorSpecification.hasPreference(preference));
        }

        Page<Assessor> results = assessorRepository.findAll(spec, page);

        return results.map(r -> new AssessorDto(r, uploadedFilesUrl));
    }

    @Override
    public AssessorDto findByUuid(UUID uuid) {
        Assessor entity = assessorRepository.findByUuid(uuid)
                .orElseThrow(() -> new ValidationException("Assessor not found."));
        return new AssessorDto(entity, uploadedFilesUrl);
    }

    @Override
    public AssessorDto getById(Long id) {
        Assessor entity = assessorRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Assessor not found."));
        return new AssessorDto(entity, uploadedFilesUrl);
    }

    @Override
    public Optional<Assessor> findById(Long id) {
        return assessorRepository.findById(id);
    }

    @Override
    public void delete(UUID uuid) {
        assessorRepository.deleteByUuid(uuid);
    }

    @Override
    public int calculateProfileCompletion(Long assessorId) {
        Assessor assessor = assessorRepository.findById(assessorId)
                .orElseThrow(() -> new ValidationException("Assessor not found"));

        double score = 0.0;

        // Weights
        final double PERSONAL_INFO_WEIGHT = 5.0;
        final double PHOTO_WEIGHT = 5.0;
        final double EDUCATION_WEIGHT = 40;
        final double EMPLOYMENT_WEIGHT = 5.0;
        final double REFERENCE_WEIGHT = 5.0;
        final double CV_WEIGHT = 25;
        final double CERTIFICATION_WEIGHT = 5;
        final double IDS_WEIGHT = 5;
        final double CONTACTS_WEIGHT = 5;

        // Personal Info
        if (isPersonalInfoComplete(assessor)) {
            score += PERSONAL_INFO_WEIGHT;
        }

        // Contacts Info
        if (isContactsComplete(assessor)) {
            score += CONTACTS_WEIGHT;
        }

        // ids Info
        if (isIdsComplete(assessor)) {
            score += IDS_WEIGHT;
        }

        // Profile Photo
        if (notBlank(assessor.getPhoto())) {
            score += PHOTO_WEIGHT;
        }

        // Education History
        List<EducationBackgroundDto> educationBackgrounds = this.educationBackground(assessorId);
        if (!educationBackgrounds.isEmpty()) {
            score += EDUCATION_WEIGHT;
        }

        // Employment History
        List<EmploymentHistoryDto> employmentHistory = this.employmentHistory(assessorId);
        if (!employmentHistory.isEmpty()) {
            score += EMPLOYMENT_WEIGHT;
        }

        // References
        List<AssessorReferenceDto> references = this.references(assessorId);
        if (!references.isEmpty()) {
            score += REFERENCE_WEIGHT;
        }

        // Uploaded Documents
        List<AssessorDocumentDto> documents = this.documents(assessorId);
        if (!documents.isEmpty()) {
            // CV
            boolean hasCV = documents.stream().anyMatch(AssessorDocumentDto::getIsCv);
            if (hasCV) {
                score += CV_WEIGHT;
            }
        }

        // Certifications
        List<AssessorCertificationDto> certifications = this.certifications(assessorId);
        if (!certifications.isEmpty()) {
            score += CERTIFICATION_WEIGHT;
        }

        return (int) Math.round(score);
    }

    private boolean isPersonalInfoComplete(Assessor a) {
        return notBlank(a.getFirstName())
                && notBlank(a.getLastName())
                && notBlank(a.getLastName())
                && notBlank(a.getTitle())
                && a.getSex() != null
                && a.getDob() != null;
    }

    private boolean isContactsComplete(Assessor a) {
        return notBlank(a.getEmail())
                && notBlank(a.getPhone()) && a.getLocationId() != null;
    }

    private boolean isIdsComplete(Assessor a) {
        return notBlank(a.getIdentificationId())
                && a.getIdentificationType() != null;
    }

    private boolean notBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    @Override
    public List<EducationBackgroundDto> educationBackground(Long assessorId) {
        Specification<EducationBackground> spec = Specification.where(EducationBackgroundSpecification
                .byAssessor(assessorId).and(EducationBackgroundSpecification.notDeleted()));
        return educationBackgroundRepository.findAll(spec).stream().map(EducationBackgroundDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmploymentHistoryDto> employmentHistory(Long assessorId) {
        Specification<EmploymentHistory> spec = Specification.where(
                EmploymentHistorySpecification.byAssessor(assessorId).and(EmploymentHistorySpecification.notDeleted()));
        return employmentHistoryRepository.findAll(spec).stream().map(EmploymentHistoryDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssessorReferenceDto> references(Long assessorId) {
        Specification<AssessorReference> spec = Specification.where(
                AssessorReferenceSpecification.byAssessor(assessorId).and(AssessorReferenceSpecification.notDeleted()));
        return assessorReferenceRepository.findAll(spec).stream().map(AssessorReferenceDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssessorDocumentDto> documents(Long assessorId) {
        Specification<AssessorDocument> spec = Specification.where(
                AssessorDocumentSpecification.byAssessor(assessorId).and(AssessorDocumentSpecification.notDeleted()));
        return assessorDocumentRepository.findAll(spec).stream().map(r -> new AssessorDocumentDto(r, uploadedFilesUrl))
                .collect(Collectors.toList());
    }

    @Override
    public Page<HotelResponseDto> assessmentAssignments(Map<String, String> filter, Pageable page) {
        long assessorId = 0L;
        long hotelId = 0L;
        LocalDate fromDate = null;
        LocalDate toDate = null;
        AssessmentStatus status = null;
        if (filter != null && !filter.isEmpty()) {
            assessorId = Long.parseLong(filter.get("assessorId") != null ? filter.get("assessorId") : "0");
            hotelId = Long.parseLong(filter.get("hotelId") != null ? filter.get("hotelId") : "0");
            if (filter.get("fromDate") != null) {
                fromDate = LocalDate.parse(filter.get("fromDate"));
            }
            if (filter.get("toDate") != null) {
                toDate = LocalDate.parse(filter.get("toDate"));
            }
            if (filter.get("status") != null) {
                status = AssessmentStatus.valueOf(filter.get("status") != null ? filter.get("status") : "Pending");
            }
        }

        Specification<AssessorHotel> spec = Specification.where(null);
        if (assessorId > 0) {
            spec = spec.and(AssessorHotelSpecification.byAssessor(assessorId));
        }

        if (hotelId > 0) {
            spec = spec.and(AssessorHotelSpecification.byHotel(hotelId));
        }
        if (fromDate != null && toDate != null) {
            spec = spec.and(AssessorHotelSpecification.byDateRange(fromDate, toDate));
        }
        if (status != null) {
            spec = spec.and(AssessorHotelSpecification.byStatus(status));
        }

        Page<AssessorHotel> results = assessorHotelRepository.findAll(spec, page);

        return results.map(r -> new HotelResponseDto(r.getHotel(), r, uploadedFilesUrl));
    }

    @Override
    public Page<HotelResponseDto> newAssignments(Map<String, String> filter, Pageable page) {
        long assessorId = 0L;
        long hotelId = 0L;
        LocalDate fromDate = null;
        LocalDate toDate = null;
        boolean selfAssessmentRequest = false;
        if (filter != null && !filter.isEmpty()) {
            assessorId = Long.parseLong(filter.get("assessorId") != null ? filter.get("assessorId") : "0");
            hotelId = Long.parseLong(filter.get("hotelId") != null ? filter.get("hotelId") : "0");
            if (filter.get("fromDate") != null) {
                fromDate = LocalDate.parse(filter.get("fromDate"));
            }
            if (filter.get("toDate") != null) {
                toDate = LocalDate.parse(filter.get("toDate"));
            }

            if (filter.get("selfAssessmentRequest") != null) {
                selfAssessmentRequest = Boolean.parseBoolean(filter.get("selfAssessmentRequest"));
            }
        }

        Specification<AssessorHotel> spec = Specification.where(null);
        if (assessorId > 0) {
            spec = spec.and(AssessorHotelSpecification.byAssessor(assessorId));
        }

        if (hotelId > 0) {
            spec = spec.and(AssessorHotelSpecification.byHotel(hotelId));
        }
        if (fromDate != null && toDate != null) {
            spec = spec.and(AssessorHotelSpecification.byDateRange(fromDate, toDate));
        }

        spec = spec.and(AssessorHotelSpecification.byStatus(AssessmentStatus.Pending));
        spec = spec.and(AssessorHotelSpecification.dataNotCollected());
        spec = spec.and(AssessorHotelSpecification.selfAssessment(selfAssessmentRequest));

        Page<AssessorHotel> results = assessorHotelRepository.findAll(spec, page);

        return results.map(r -> new HotelResponseDto(r.getHotel(), r, uploadedFilesUrl));
    }

    @Override
    public Page<HotelResponseDto> submittedAssignments(Map<String, String> filter, Pageable page) {
        long assessorId = 0L;
        long hotelId = 0L;
        LocalDate fromDate = null;
        LocalDate toDate = null;
        if (filter != null && !filter.isEmpty()) {
            assessorId = Long.parseLong(filter.get("assessorId") != null ? filter.get("assessorId") : "0");
            hotelId = Long.parseLong(filter.get("hotelId") != null ? filter.get("hotelId") : "0");
            if (filter.get("fromDate") != null) {
                fromDate = LocalDate.parse(filter.get("fromDate"));
            }
            if (filter.get("toDate") != null) {
                toDate = LocalDate.parse(filter.get("toDate"));
            }
        }

        Specification<AssessorHotel> spec = Specification.where(null);
        if (assessorId > 0) {
            spec = spec.and(AssessorHotelSpecification.byAssessor(assessorId));
        }

        if (hotelId > 0) {
            spec = spec.and(AssessorHotelSpecification.byHotel(hotelId));
        }
        if (fromDate != null && toDate != null) {
            spec = spec.and(AssessorHotelSpecification.byDateRange(fromDate, toDate));
        }

        spec = spec.and(AssessorHotelSpecification.byStatus(AssessmentStatus.Pending));
        spec = spec.and(AssessorHotelSpecification.dataCollected());

        Page<AssessorHotel> results = assessorHotelRepository.findAll(spec, page);

        return results.map(r -> new HotelResponseDto(r.getHotel(), r, uploadedFilesUrl));
    }

    @Override
    public Page<HotelResponseDto> completedAssignments(Map<String, String> filter, Pageable page) {
        long assessorId = 0L;
        long hotelId = 0L;
        LocalDate fromDate = null;
        LocalDate toDate = null;
        if (filter != null && !filter.isEmpty()) {
            assessorId = Long.parseLong(filter.get("assessorId") != null ? filter.get("assessorId") : "0");
            hotelId = Long.parseLong(filter.get("hotelId") != null ? filter.get("hotelId") : "0");
            if (filter.get("fromDate") != null) {
                fromDate = LocalDate.parse(filter.get("fromDate"));
            }
            if (filter.get("toDate") != null) {
                toDate = LocalDate.parse(filter.get("toDate"));
            }
        }

        Specification<AssessorHotel> spec = Specification.where(null);
        if (assessorId > 0) {
            spec = spec.and(AssessorHotelSpecification.byAssessor(assessorId));
        }

        if (hotelId > 0) {
            spec = spec.and(AssessorHotelSpecification.byHotel(hotelId));
        }
        if (fromDate != null && toDate != null) {
            spec = spec.and(AssessorHotelSpecification.byDateRange(fromDate, toDate));
        }

        spec = spec.and(AssessorHotelSpecification.byStatus(AssessmentStatus.Completed));
        spec = spec.and(AssessorHotelSpecification.dataCollected());

        Page<AssessorHotel> results = assessorHotelRepository.findAll(spec, page);

        return results.map(r -> new HotelResponseDto(r.getHotel(), r, uploadedFilesUrl));
    }

    @Override
    public List<AssessorCertificationDto> certifications(Long assessorId) {
        Specification<AssessorCertification> spec = Specification.where(AssessorCertificationSpecification
                .byAssessor(assessorId).and(AssessorCertificationSpecification.notDeleted()));
        return assessorCertificationRepository.findAll(spec).stream().map(AssessorCertificationDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public void setProfilePhoto(Long assessorId, Long fileUploadId) {
        Assessor assessor = assessorRepository.findById(assessorId)
                .orElseThrow(() -> new ValidationException("Assessor with id {" + assessorId + "} not found"));

        if (fileUploadId != null) {
            FileUpload fileUpload = fileUploadRepository.findById(fileUploadId)
                    .orElseThrow(() -> new ValidationException("FileUpload with id {" + fileUploadId + "} not found"));
            assessor.setProfilePhoto(fileUpload);
        } else {
            assessor.setProfilePhoto(null);
        }

        assessorRepository.save(assessor);
    }

    @Override
    public void assignHotels(Long id, AssessorHotelDto dto) {
        Assessor assessor = assessorRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("Assessor with id {" + id + "} not found"));

        for (Long hotelId : dto.getHotels()) {
            Optional<AssessorHotel> row = assessorHotelRepository.findByAssessorIdAndHotelId(id, hotelId);
            if (row.isEmpty()) {
                AssessorHotel assessorHotel = new AssessorHotel();
                assessorHotel.setAssessorId(assessor.getId());
                assessorHotel.setHotelId(hotelId);
                assessorHotel.setSelfAssessmentRequest(dto.getSelfAssessmentRequest());
                assessorHotel.setStatus(AssessmentStatus.Pending);
                assessorHotel.setDateAssigned(dto.getDateAssigned() != null ? dto.getDateAssigned() : LocalDate.now());
                assessorHotel.setDeadline(dto.getDeadline() != null ? dto.getDeadline() : LocalDate.now().plusWeeks(2));
                assessorHotelRepository.save(assessorHotel);
            }
        }
    }
}
