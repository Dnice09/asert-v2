package tz.go.mnrt.asert.modules.form.formsubmission.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.form.form.entity.Form;
import tz.go.mnrt.asert.modules.form.formfieldresponse.entity.FormFieldResponse;
import tz.go.mnrt.asert.modules.form.formsubmission.enums.SubmissionStatus;
import tz.go.mnrt.asert.modules.form.formsubmissionscore.entity.FormSubmissionScore;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "form_submissions")
public class FormSubmission extends BaseModel {
    @Column(name = "submitted_by", nullable = false)
    private String submittedBy;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @ManyToOne
    private Form form;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FormFieldResponse> responses;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FormSubmissionScore> sectionScores;

    @Column(name = "total_score")
    private Double totalScore;

    @Column(name = "max_possible_score")
    private Double maxPossibleScore;

    @Column(name = "percentage_score")
    private Double percentage;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "assessor_id")
    private Assessor assessor;

    // Variance tracking fields
    @Column(name = "variance_check_status", length = 50)
    private String varianceCheckStatus;

    @Column(name = "variance_resolution_version")
    private Integer varianceResolutionVersion;

    @Column(name = "approved_for_dt_review")
    private Boolean approvedForDtReview;

    // Historical variance status - captures whether there were unresolved variances
    // at the moment this submission was created (temporal/point-in-time data)
    @Column(name = "has_unresolved_variances")
    private Boolean hasUnresolvedVariances;

    // Submission workflow status fields
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SubmissionStatus status = SubmissionStatus.DRAFT;

    @Column(name = "submitted_for_approval_at")
    private LocalDateTime submittedForApprovalAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Builder
    public FormSubmission(String submittedBy, LocalDateTime submittedAt, Form form, Hotel hotel) {
        this.submittedBy = submittedBy;
        this.submittedAt = submittedAt;
        this.form = form;
        this.hotel = hotel;
        this.status = SubmissionStatus.DRAFT; // Initialize with DRAFT status
    }
}
