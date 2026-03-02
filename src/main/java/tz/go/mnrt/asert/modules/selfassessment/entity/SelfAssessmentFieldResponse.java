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
import tz.go.mnrt.asert.modules.form.formfield.entity.FormField;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "self_assessment_field_responses")
public class SelfAssessmentFieldResponse extends BaseModel {

    @ManyToOne(optional = false)
    @JoinColumn(name = "self_assessment_id", nullable = false)
    private SelfAssessment selfAssessment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "field_id", nullable = false)
    private FormField field;

    @Column(name = "value", columnDefinition = "TEXT")
    private String value;
}
