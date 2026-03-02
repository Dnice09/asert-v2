package tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.entity;

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
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.form.form.entity.Form;
import tz.go.mnrt.asert.modules.form.formsubmission.entity.FormSubmission;
import tz.go.mnrt.asert.modules.user.entity.User;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hotel_assessment_approvals")
public class HotelAssessmentApproval extends BaseModel {

    @ManyToOne(optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne(optional = false)
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    // Form submission references (3 assessors)
    @ManyToOne
    @JoinColumn(name = "submission_1_id")
    private FormSubmission submission1;

    @ManyToOne
    @JoinColumn(name = "submission_2_id")
    private FormSubmission submission2;

    @ManyToOne
    @JoinColumn(name = "submission_3_id")
    private FormSubmission submission3;

    // Calculated final scores (average of 3 submissions)
    @Column(name = "final_total_score", precision = 10, scale = 2)
    private Double finalTotalScore;

    @Column(name = "final_max_score", precision = 10, scale = 2)
    private Double finalMaxScore;

    @Column(name = "final_percentage", precision = 5, scale = 2)
    private Double finalPercentage;

    @Column(name = "final_rating", length = 50)
    private String finalRating;

    // Variance status
    @Column(name = "has_unresolved_variances")
    private Boolean hasUnresolvedVariances;

    @Column(name = "variance_resolution_count")
    private Integer varianceResolutionCount;

    @Column(name = "all_variances_resolved_at")
    private LocalDateTime allVariancesResolvedAt;

    // DT Approval workflow
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "submitted_to_dt_at")
    private LocalDateTime submittedToDtAt;

    @ManyToOne
    @JoinColumn(name = "approved_by_dt_id")
    private User approvedByDt;

    @Column(name = "dt_approval_date")
    private LocalDateTime dtApprovalDate;

    @Column(name = "dt_rejection_reason", columnDefinition = "TEXT")
    private String dtRejectionReason;

    @Column(name = "dt_comments", columnDefinition = "TEXT")
    private String dtComments;
}
