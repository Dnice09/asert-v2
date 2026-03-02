package tz.go.mnrt.asert.modules.setup.assessorrejectionreason.entity;

import lombok.*;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "assessor_rejection_reasons")
public class AssessorRejectionReason extends BaseModel {
    @Column(name = "code", nullable = false)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String reason;
}
