package tz.go.mnrt.asert.modules.selfassessment.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.form.form.entity.Form;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "self_assessments")
public class SelfAssessment extends BaseModel {

    @ManyToOne(optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne(optional = false)
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    @Column(name = "submitted_by", nullable = false)
    private String submittedBy;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "total_score")
    private Double totalScore;

    @Column(name = "max_possible_score")
    private Double maxPossibleScore;

    @Column(name = "percentage")
    private Double percentage;

    @Column(name = "estimated_rating", length = 100)
    private String estimatedRating;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private SelfAssessmentStatus status = SelfAssessmentStatus.DRAFT;

    @Column(name = "is_official", nullable = false)
    @Builder.Default
    private Boolean isOfficial = false;

    @OneToMany(mappedBy = "selfAssessment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SelfAssessmentScore> sectionScores = new ArrayList<>();

    @OneToMany(mappedBy = "selfAssessment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SelfAssessmentFieldResponse> fieldResponses = new ArrayList<>();
}
