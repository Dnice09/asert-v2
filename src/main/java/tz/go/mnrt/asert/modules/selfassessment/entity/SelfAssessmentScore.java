package tz.go.mnrt.asert.modules.selfassessment.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
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

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "self_assessment_scores")
public class SelfAssessmentScore extends BaseModel {

    @ManyToOne(optional = false)
    @JoinColumn(name = "self_assessment_id", nullable = false)
    private SelfAssessment selfAssessment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private FormSection section;

    @Column(name = "score")
    private Double score;

    @Column(name = "max_possible")
    private Double maxPossible;

    @Column(name = "percentage")
    private Double percentage;
}
