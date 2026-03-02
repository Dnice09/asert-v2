package tz.go.mnrt.asert.modules.form.formsubmissionscore.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.form.formsection.entity.FormSection;
import tz.go.mnrt.asert.modules.form.formsubmission.entity.FormSubmission;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "form_submission_scores")
public class FormSubmissionScore extends BaseModel {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private FormSubmission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private FormSection section;

    // Calculated score for this section
    @Column(name = "score")
    private Double score;

    // Maximum possible score for this section
    @Column(name = "max_possible")
    private Double maxPossible;

    // Score as a percentage of maximum
    @Column(name = "percentage")
    private Double percentage;
}
