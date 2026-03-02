package tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.form.form.entity.Form;
import tz.go.mnrt.asert.modules.form.formfield.entity.FormField;
import tz.go.mnrt.asert.modules.form.formsection.entity.FormSection;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "assessment_variance_logs")
public class AssessmentVarianceLog extends BaseModel {

    @ManyToOne(optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne(optional = false)
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    @ManyToOne(optional = false)
    @JoinColumn(name = "form_field_id", nullable = true)
    private FormField formField;

    @Column(name = "field_uuid", nullable = false)
    private UUID fieldUuid;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private FormSection section;

    @Column(name = "variance_type", nullable = false, length = 50)
    private String varianceType;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "score_difference", nullable = false, precision = 10, scale = 2)
    private Double scoreDifference;

    // Assessor 1
    @ManyToOne
    @JoinColumn(name = "assessor_1_id")
    private Assessor assessor1;

    @Column(name = "assessor_1_score", precision = 10, scale = 2)
    private Double assessor1Score;

    // Assessor 2
    @ManyToOne
    @JoinColumn(name = "assessor_2_id")
    private Assessor assessor2;

    @Column(name = "assessor_2_score", precision = 10, scale = 2)
    private Double assessor2Score;

    // Assessor 3
    @ManyToOne
    @JoinColumn(name = "assessor_3_id")
    private Assessor assessor3;

    @Column(name = "assessor_3_score", precision = 10, scale = 2)
    private Double assessor3Score;

    @Column(name = "detected_at")
    private LocalDateTime detectedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}
