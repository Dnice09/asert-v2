package tz.go.mnrt.asert.modules.form.formdraft.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tz.go.mnrt.asert.modules.form.formdraft.entity.FormDraft;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormDraftResponseDto {

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

    public FormDraftResponseDto(FormDraft formDraft) {
        this.id = formDraft.getId();
        this.uuid = formDraft.getUuid();
        this.formUuid = formDraft.getForm().getUuid();
        this.formName = formDraft.getForm().getName();
        if (formDraft.getHotel() != null) {
            this.hotelUuid = formDraft.getHotel().getUuid();
            this.hotelName = formDraft.getHotel().getName();
        }
        this.submittedBy = formDraft.getSubmittedBy();
        this.currentSectionIndex = formDraft.getCurrentSectionIndex();
        this.formData = formDraft.getFormData();
        this.lastSavedAt = formDraft.getLastSavedAt();
        this.completionPercentage = formDraft.getCompletionPercentage();
        this.totalSections = formDraft.getTotalSections();
    }
}
