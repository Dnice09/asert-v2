package tz.go.mnrt.asert.modules.assessment.assessmentrequest.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.user.entity.User;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assessment_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentRequest extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false, foreignKey = @ForeignKey(name = "fk_assessment_request_hotel"))
    private Hotel hotel;

    @Column(name = "contact_person", nullable = false)
    private String contactPerson;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "requested_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate requestedDate;

    @Column(name = "additional_comments", columnDefinition = "TEXT")
    private String additionalComments;

    @Column(name = "terms_accepted", nullable = false)
    private Boolean termsAccepted = false;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AssessmentRequestStatus status = AssessmentRequestStatus.PENDING;

    @Column(name = "submitted_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submittedAt;

    @Column(name = "review_started_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewStartedAt;

    @Column(name = "processed_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processedAt;

    @Column(name = "processed_by")
    private Long processedBy;

    @Column(name = "processing_notes", columnDefinition = "TEXT")
    private String processingNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by", foreignKey = @ForeignKey(name = "fk_assessment_request_user"))
    private User submittedByUser;

    @OneToMany(mappedBy = "assessmentRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<EssentialItem> essentialItems = new ArrayList<>();


    // Helper methods
    public void addEssentialItem(EssentialItem item) {
        essentialItems.add(item);
        item.setAssessmentRequest(this);
    }

    public void removeEssentialItem(EssentialItem item) {
        essentialItems.remove(item);
        item.setAssessmentRequest(null);
    }


    public String getFacilityName() {
        return hotel != null ? hotel.getName() : null;
    }

    public String getFacilityType() {
        return hotel != null && hotel.getPropertyType() != null ? hotel.getPropertyType().name() : null;
    }

    public int getCompliantItemsCount() {
        return (int) essentialItems.stream()
                .filter(item -> "compliant".equals(item.getCompliance()))
                .count();
    }

    public int getNonCompliantItemsCount() {
        return (int) essentialItems.stream()
                .filter(item -> "non-compliant".equals(item.getCompliance()))
                .count();
    }

    public int getPendingItemsCount() {
        return (int) essentialItems.stream()
                .filter(item -> item.getCompliance() == null || item.getCompliance().isEmpty())
                .count();
    }

    public double getCompletionPercentage() {
        if (essentialItems.isEmpty()) {
            return 0.0;
        }
        long completedItems = essentialItems.stream()
                .filter(item -> item.getCompliance() != null && !item.getCompliance().isEmpty())
                .count();
        return (double) completedItems / essentialItems.size() * 100.0;
    }
}
