package tz.go.mnrt.asert.modules.assessment.assessmentrequest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.entity.AssessmentRequest;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.entity.AssessmentRequestStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentRequestResponseDto {

    private Long id;
    private UUID uuid;
    private UUID hotelUuid;
    private String facilityName;
    private String facilityType;
    private String contactPerson;
    private String email;
    private String phoneNumber;
    private String address;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate requestedDate;

    private String additionalComments;
    private Boolean termsAccepted;
    private AssessmentRequestStatus status;
    private String statusDisplayName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submittedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processedAt;

    private Long processedBy;
    private String processingNotes;
    private String submittedByUserName;
    private Long submittedByUserId;

    private List<EssentialItemResponseDto> essentialItems;
    private List<AssessmentRequestDocumentResponseDto> uploadedDocuments;

    // Summary statistics
    private int compliantItemsCount;
    private int nonCompliantItemsCount;
    private int pendingItemsCount;
    private double completionPercentage;
    private int totalItemsCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public AssessmentRequestResponseDto(AssessmentRequest entity) {
        BeanUtils.copyProperties(entity, this);
        
        this.statusDisplayName = entity.getStatus().getDisplayName();
        this.facilityName = entity.getFacilityName();
        this.facilityType = entity.getFacilityType();
        
        if (entity.getHotel() != null) {
            this.hotelUuid = entity.getHotel().getUuid();
        }
        
        if (entity.getSubmittedByUser() != null) {
            this.submittedByUserName = entity.getSubmittedByUser().getFirstName() + " " + 
                                     entity.getSubmittedByUser().getLastName();
            this.submittedByUserId = entity.getSubmittedByUser().getId();
        }

        if (entity.getEssentialItems() != null) {
            this.essentialItems = entity.getEssentialItems().stream()
                    .map(EssentialItemResponseDto::new)
                    .collect(Collectors.toList());
        }

        // Note: uploadedDocuments will be populated by the service layer
        // since it requires FileUpload repository access to retrieve file details

        // Calculate summary statistics
        this.compliantItemsCount = entity.getCompliantItemsCount();
        this.nonCompliantItemsCount = entity.getNonCompliantItemsCount();
        this.pendingItemsCount = entity.getPendingItemsCount();
        this.completionPercentage = entity.getCompletionPercentage();
        this.totalItemsCount = entity.getEssentialItems() != null ? entity.getEssentialItems().size() : 0;
    }
}