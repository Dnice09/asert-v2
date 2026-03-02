package tz.go.mnrt.asert.modules.assessment.assessorvariancenotification.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.entity.AssessmentVarianceLog;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "assessor_variance_notifications")
public class AssessorVarianceNotification extends BaseModel {

    @ManyToOne(optional = false)
    @JoinColumn(name = "assessment_variance_log_id", nullable = false)
    private AssessmentVarianceLog assessmentVarianceLog;

    @ManyToOne(optional = false)
    @JoinColumn(name = "assessor_id", nullable = false)
    private Assessor assessor;

    @Column(name = "notification_type", nullable = false, length = 50)
    private String notificationType;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}
