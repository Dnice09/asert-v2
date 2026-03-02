package tz.go.mnrt.asert.modules.assessment.certification.entity;

import lombok.*;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "assessor_certifications")
public class AssessorCertification extends BaseModel {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String issuer;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;


    @ManyToOne
    @JoinColumn(name = "assessor_id", nullable = false, insertable = false, updatable = false)
    private Assessor assessor;

    @Column(name = "assessor_id", nullable = false)
    private Long assessorId;
}
