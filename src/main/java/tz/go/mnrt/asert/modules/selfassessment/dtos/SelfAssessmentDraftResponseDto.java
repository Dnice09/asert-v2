package tz.go.mnrt.asert.modules.selfassessment.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tz.go.mnrt.asert.modules.selfassessment.entity.SelfAssessmentDraft;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelfAssessmentDraftResponseDto {

    private Long id;
    private UUID uuid;

    private UUID formUuid;
    private String formName;
    private UUID hotelUuid;
    private String hotelName;
    private String submittedBy;
    private Integer currentSectionIndex;
    private String formData;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastSavedAt;

    private Double completionPercentage;
    private Integer totalSections;

    public SelfAssessmentDraftResponseDto(SelfAssessmentDraft draft) {
        this.id = draft.getId();
        this.uuid = draft.getUuid();
        this.formUuid = draft.getForm().getUuid();
        this.formName = draft.getForm().getName();
        if (draft.getHotel() != null) {
            this.hotelUuid = draft.getHotel().getUuid();
            this.hotelName = draft.getHotel().getName();
        }
        this.submittedBy = draft.getSubmittedBy();
        this.currentSectionIndex = draft.getCurrentSectionIndex();
        this.formData = draft.getFormData();
        this.lastSavedAt = draft.getLastSavedAt();
        this.completionPercentage = draft.getCompletionPercentage();
        this.totalSections = draft.getTotalSections();
    }
}
