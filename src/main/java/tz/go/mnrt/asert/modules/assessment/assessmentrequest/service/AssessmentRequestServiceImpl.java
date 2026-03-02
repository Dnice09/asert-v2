package tz.go.mnrt.asert.modules.assessment.assessmentrequest.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.dto.AssessmentRequestCreateDto;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.dto.AssessmentRequestDocumentResponseDto;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.dto.AssessmentRequestResponseDto;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.dto.UploadedDocumentDto;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.entity.AssessmentRequest;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.entity.AssessmentRequestStatus;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.entity.EssentialItem;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.entity.EssentialItemEvidence;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.repository.AssessmentRequestRepository;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.hotel.hotel.repository.HotelRepository;
import tz.go.mnrt.asert.modules.hotel.hotel.services.HotelService;
import tz.go.mnrt.asert.modules.hotel.hotel.state.HotelState;
import tz.go.mnrt.asert.modules.setup.fileupload.entity.FileUpload;
import tz.go.mnrt.asert.modules.setup.fileupload.repository.FileUploadRepository;
import tz.go.mnrt.asert.modules.user.dtos.LoggedInUserDto;
import tz.go.mnrt.asert.modules.user.entity.User;
import tz.go.mnrt.asert.modules.user.repository.UserRepository;
import tz.go.mnrt.asert.modules.user.service.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AssessmentRequestServiceImpl extends SimpleSearchService<AssessmentRequest>
        implements AssessmentRequestService {

    private final AssessmentRequestRepository assessmentRequestRepository;
    private final UserService userService;
    private final HotelService hotelService;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final FileUploadRepository fileUploadRepository;

    @Override
    public AssessmentRequestResponseDto create(AssessmentRequestCreateDto createDto) {
        log.info("Creating new assessment request for hotel UUID: {}", createDto.getHotelUuid());

        LoggedInUserDto currentUser = userService.loggedIn().orElse(null);

        User user = userRepository.findByUuid(currentUser.getUuid())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Hotel hotel = hotelService.findEntityByUuid(createDto.getHotelUuid());

        AssessmentRequest assessmentRequest = new AssessmentRequest();
        assessmentRequest.setUuid(UUID.randomUUID());
        assessmentRequest.setHotel(hotel);
        assessmentRequest.setContactPerson(createDto.getFacilityInfo().getContactPerson());
        assessmentRequest.setPhoneNumber(createDto.getFacilityInfo().getPhoneNumber());
        assessmentRequest.setEmail(createDto.getFacilityInfo().getEmail());
        assessmentRequest.setAddress(createDto.getFacilityInfo().getAddress());
        assessmentRequest.setRequestedDate(createDto.getFacilityInfo().getRequestedDate());
        assessmentRequest.setAdditionalComments(createDto.getAdditionalComments());
        assessmentRequest.setTermsAccepted(createDto.getTermsAccepted());
        assessmentRequest.setSubmittedByUser(user);
        assessmentRequest
                .setSubmittedAt(createDto.getSubmittedAt() != null ? createDto.getSubmittedAt() : LocalDateTime.now());
        assessmentRequest.setStatus(AssessmentRequestStatus.SCHEDULED);

        List<EssentialItem> essentialItems = createDto.getEssentialItems().stream()
                .map(itemDto -> {
                    EssentialItem item = new EssentialItem();
                    item.setUuid(UUID.randomUUID());
                    item.setItemNo(itemDto.getItemNo());
                    item.setDescription(itemDto.getDescription());
                    item.setComplianceRequirement(itemDto.getComplianceRequirement());
                    item.setCompliance(itemDto.getCompliance());
                    item.setEvidenceProvided(itemDto.getEvidenceProvided());
                    item.setEvidenceType(itemDto.getEvidenceType());
                    item.setNotes(itemDto.getNotes());
                    item.setAssessmentRequest(assessmentRequest);

                    // Handle uploaded documents separately if needed
                    // Evidence files are now handled through uploadedDocuments array

                    return item;
                })
                .collect(Collectors.toList());

        assessmentRequest.setEssentialItems(essentialItems);

        // Save the assessment request first to get IDs
        AssessmentRequest savedRequest = assessmentRequestRepository.save(assessmentRequest);

        // Handle uploaded documents - link them to appropriate essential items
        if (createDto.getUploadedDocuments() != null && !createDto.getUploadedDocuments().isEmpty()) {
            linkUploadedFilesToEssentialItems(savedRequest, createDto.getUploadedDocuments());
            log.info("Linked {} uploaded documents to essential items for assessment request", createDto.getUploadedDocuments().size());
        }
        log.info("Assessment request created with UUID: {}", savedRequest.getUuid());

        return createResponseDto(savedRequest, createDto.getUploadedDocuments());
    }

    @Override
    public AssessmentRequestResponseDto findByUuid(UUID uuid) {
        AssessmentRequest assessmentRequest = assessmentRequestRepository.findByUuidWithEssentialItems(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Assessment request not found with UUID: " + uuid));

        // Force initialization of lazy-loaded evidence collections
        List<UploadedDocumentDto> uploadedDocuments = new ArrayList<>();
        for (EssentialItem item : assessmentRequest.getEssentialItems()) {
            // Initialize the evidence collection
            item.getEvidence().size(); // Force Hibernate to load the collection

            for (EssentialItemEvidence evidence : item.getEvidence()) {
                if (evidence.getFileUpload() != null) {
                    UploadedDocumentDto docDto = new UploadedDocumentDto();
                    docDto.setAttachmentId(evidence.getFileUpload().getId());
                    docDto.setName(evidence.getFileName());
                    docDto.setFileSize(evidence.getFileSize());
                    docDto.setUploadType(getUploadTypeForItem(item.getItemNo()));
                    docDto.setFileType(evidence.getMimeType());
                    uploadedDocuments.add(docDto);
                }
            }
        }

        return createResponseDto(assessmentRequest, uploadedDocuments);
    }

    /**
     * Maps essential item numbers back to upload types
     */
    private String getUploadTypeForItem(Integer itemNo) {
        Map<Integer, String> itemNumberToUploadType = Map.ofEntries(
            Map.entry(1, "BUILDING_PLAN_DOCUMENTS"),
            Map.entry(2, "OPERATING_LICENSES"),
            Map.entry(3, "EIA_REPORTS_AUDITS"),
            Map.entry(11, "VERMIN_PROOFING_DOCS"),
            Map.entry(12, "WATER_SUPPLY_DOCS"),
            Map.entry(16, "ELECTRICAL_SAFETY_DOCS"),
            Map.entry(18, "QUALIFIED_MANAGEMENT_DOCS"),
            Map.entry(19, "QUALIFIED_DEPARTMENTAL_HEADS_DOCS"),
            Map.entry(20, "HEALTH_MEDICAL_EXAM_DOCS"),
            Map.entry(22, "INSURANCE_DOCS")
        );

        return itemNumberToUploadType.getOrDefault(itemNo, "UNKNOWN");
    }

    @Override
    public Page<AssessmentRequestResponseDto> findAll(Pageable page, Map<String, String> search) {
        return assessmentRequestRepository.findAll(createSpecification(AssessmentRequest.class, search), page)
                .map(entity -> createResponseDto(entity, null));
    }

    private AssessmentRequestResponseDto createResponseDto(AssessmentRequest entity,
            List<UploadedDocumentDto> uploadedDocuments) {
        AssessmentRequestResponseDto responseDto = new AssessmentRequestResponseDto(entity);

        // Populate uploaded documents if provided (for create operations)
        if (uploadedDocuments != null && !uploadedDocuments.isEmpty()) {
            List<AssessmentRequestDocumentResponseDto> documentResponses = uploadedDocuments.stream()
                    .map(docDto -> {
                        FileUpload fileUpload = fileUploadRepository.findById(docDto.getAttachmentId())
                                .orElse(null);
                        if (fileUpload != null) {
                            AssessmentRequestDocumentResponseDto docResponse = new AssessmentRequestDocumentResponseDto(
                                    fileUpload, docDto.getUploadType(), docDto.getName());
                            docResponse.setAssessmentRequestId(entity.getId());
                            docResponse.setAssessmentRequestUuid(entity.getUuid().toString());
                            return docResponse;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            responseDto.setUploadedDocuments(documentResponses);
        } else {
            responseDto.setUploadedDocuments(new ArrayList<>());
        }

        return responseDto;
    }

    @Override
    public AssessmentRequestResponseDto approve(UUID uuid) {
        AssessmentRequest assessmentRequest = assessmentRequestRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Assessment request not found with UUID: " + uuid));

        // Check completion percentage - must be at least 75%
        double completionPercentage = assessmentRequest.getCompletionPercentage();
        if (completionPercentage < 75.0) {
            log.warn("Assessment request with UUID: {} rejected due to insufficient completion percentage: {}%", 
                    uuid, completionPercentage);
            
            // Update the assessment request status to rejected
            assessmentRequest.setStatus(AssessmentRequestStatus.REJECTED);
            assessmentRequest.setProcessedAt(LocalDateTime.now());
            assessmentRequest.setProcessedBy(userService.loggedIn()
                    .orElseThrow(() -> new EntityNotFoundException("Logged in user not found")).getId());
            assessmentRequest.setProcessingNotes(
                String.format("Assessment request rejected due to insufficient completion percentage: %.1f%%. Minimum required: 75%%", 
                        completionPercentage));
            
            AssessmentRequest rejectedRequest = assessmentRequestRepository.save(assessmentRequest);
            return createResponseDto(rejectedRequest, null);
        }

        // find the hotel associated with this request
        // and change its status to SUBMITED_FOR_GRADING
        Hotel hotel = assessmentRequest.getHotel();
        if (hotel == null) {
            throw new EntityNotFoundException("Hotel not found for assessment request with UUID: " + uuid);
        }
        hotel.setStatus(HotelState.SUBMITED_FOR_GRADING);
        hotelRepository.save(hotel);

        // Update the assessment request status
        assessmentRequest.setStatus(AssessmentRequestStatus.APPROVED);
        assessmentRequest.setProcessedAt(LocalDateTime.now());
        assessmentRequest.setProcessedBy(userService.loggedIn()
                .orElseThrow(() -> new EntityNotFoundException("Logged in user not found")).getId());
        assessmentRequest.setProcessingNotes(
            String.format("Assessment request approved with completion percentage: %.1f%% and hotel status updated to SUBMITED_FOR_GRADING", 
                    completionPercentage));
        AssessmentRequest updatedRequest = assessmentRequestRepository.save(assessmentRequest);
        log.info("Assessment request with UUID: {} approved with completion percentage: {}% and hotel status updated to SUBMITED_FOR_GRADING", 
                uuid, completionPercentage);
        return createResponseDto(updatedRequest, null);
    }

    /**
     * Links uploaded files to the appropriate essential items based on upload type
     */
    private void linkUploadedFilesToEssentialItems(AssessmentRequest assessmentRequest, List<UploadedDocumentDto> uploadedDocuments) {
        // Map upload types to essential item numbers (matching frontend form)
        Map<String, Integer> uploadTypeToItemNumber = Map.ofEntries(
            Map.entry("BUILDING_PLAN_DOCUMENTS", 1),
            Map.entry("OPERATING_LICENSES", 2),
            Map.entry("EIA_REPORTS_AUDITS", 3),
            Map.entry("VERMIN_PROOFING_DOCS", 11),
            Map.entry("WATER_SUPPLY_DOCS", 12),
            Map.entry("ELECTRICAL_SAFETY_DOCS", 16),
            Map.entry("QUALIFIED_MANAGEMENT_DOCS", 18),
            Map.entry("QUALIFIED_DEPARTMENTAL_HEADS_DOCS", 19),
            Map.entry("HEALTH_MEDICAL_EXAM_DOCS", 20),
            Map.entry("INSURANCE_DOCS", 22)
        );

        // Group uploaded documents by upload type
        Map<String, List<UploadedDocumentDto>> documentsByType = uploadedDocuments.stream()
                .collect(Collectors.groupingBy(UploadedDocumentDto::getUploadType));

        // Create a map of essential items by item number for quick lookup
        Map<Integer, EssentialItem> itemsByNumber = assessmentRequest.getEssentialItems().stream()
                .collect(Collectors.toMap(EssentialItem::getItemNo, item -> item));

        // Link each uploaded document to its corresponding essential item
        for (Map.Entry<String, List<UploadedDocumentDto>> entry : documentsByType.entrySet()) {
            String uploadType = entry.getKey();
            List<UploadedDocumentDto> documents = entry.getValue();
            
            Integer itemNumber = uploadTypeToItemNumber.get(uploadType);
            if (itemNumber != null) {
                EssentialItem essentialItem = itemsByNumber.get(itemNumber);
                if (essentialItem != null) {
                    // Set evidence provided to true for this item
                    essentialItem.setEvidenceProvided(true);
                    
                    // Create evidence records for each uploaded file
                    for (UploadedDocumentDto docDto : documents) {
                        FileUpload fileUpload = fileUploadRepository.findById(docDto.getAttachmentId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                        "FileUpload with id " + docDto.getAttachmentId() + " not found"));
                        
                        EssentialItemEvidence evidence = new EssentialItemEvidence();
                        evidence.setUuid(UUID.randomUUID());
                        evidence.setEssentialItem(essentialItem);
                        evidence.setFileUpload(fileUpload);
                        evidence.setFileName(docDto.getName());
                        evidence.setFileSize(docDto.getFileSize());
                        evidence.setMimeType(docDto.getFileType());
                        evidence.setEvidenceType(essentialItem.getEvidenceType());
                        evidence.setEvidenceDescription("Uploaded file: " + docDto.getName());
                        
                        essentialItem.addEvidence(evidence);
                    }
                    
                    log.debug("Linked {} files to essential item #{}: {}", 
                            documents.size(), itemNumber, essentialItem.getDescription());
                } else {
                    log.warn("Essential item with number {} not found for upload type {}", itemNumber, uploadType);
                }
            } else {
                log.warn("No mapping found for upload type: {}", uploadType);
            }
        }
        
        // Save the updated assessment request with linked evidence
        assessmentRequestRepository.save(assessmentRequest);
    }
}
