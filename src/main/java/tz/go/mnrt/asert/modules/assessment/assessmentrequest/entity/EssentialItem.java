package tz.go.mnrt.asert.modules.assessment.assessmentrequest.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "essential_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EssentialItem extends BaseModel {

    @Column(name = "item_number", nullable = false)
    private Integer itemNo;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "compliance_requirement", columnDefinition = "TEXT")
    private String complianceRequirement;

    @Column(name = "compliance")
    private String compliance;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "evidence_description", columnDefinition = "TEXT")
    private String evidenceDescription;

    @Column(name = "evidence_provided", nullable = false)
    private Boolean evidenceProvided = false;

    @Column(name = "evidence_type")
    private String evidenceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_request_id", nullable = false, foreignKey = @ForeignKey(name = "fk_essential_item_assessment_request"))
    @JsonBackReference
    private AssessmentRequest assessmentRequest;

    @OneToMany(mappedBy = "essentialItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<EssentialItemEvidence> evidence = new ArrayList<>();

    public List<EssentialItemEvidence> getEvidenceFiles() {
        return evidence;
    }

    public void setEvidenceFiles(List<EssentialItemEvidence> evidenceFiles) {
        this.evidence = evidenceFiles != null ? evidenceFiles : new ArrayList<>();
    }

    // Helper methods
    public void addEvidence(EssentialItemEvidence evidenceItem) {
        evidence.add(evidenceItem);
        evidenceItem.setEssentialItem(this);
    }

    public void removeEvidence(EssentialItemEvidence evidenceItem) {
        evidence.remove(evidenceItem);
        evidenceItem.setEssentialItem(null);
    }

    public boolean isCompliant() {
        return "compliant".equalsIgnoreCase(compliance);
    }

    public boolean isNonCompliant() {
        return "non-compliant".equalsIgnoreCase(compliance);
    }

    public boolean isPending() {
        return compliance == null || compliance.trim().isEmpty();
    }

    public String getStatusDisplayName() {
        if (isCompliant()) {
            return "Compliant";
        } else if (isNonCompliant()) {
            return "Non-Compliant";
        } else {
            return "Pending";
        }
    }
}
