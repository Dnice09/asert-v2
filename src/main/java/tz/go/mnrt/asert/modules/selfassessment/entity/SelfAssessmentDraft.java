package tz.go.mnrt.asert.modules.selfassessment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.form.form.entity.Form;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "self_assessment_drafts")
public class SelfAssessmentDraft extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @Column(name = "submitted_by", nullable = false)
    private String submittedBy;

    @Column(name = "current_section_index", nullable = false)
    private Integer currentSectionIndex = 0;

    @Column(name = "form_data", columnDefinition = "TEXT")
    private String formData;

    @Column(name = "last_saved_at", nullable = false)
    private LocalDateTime lastSavedAt;

    @Column(name = "completion_percentage")
    private Double completionPercentage = 0.0;

    @Column(name = "total_sections")
    private Integer totalSections = 0;

    @PrePersist
    @PreUpdate
    public void updateLastSavedAt() {
        this.lastSavedAt = LocalDateTime.now();
    }
}
