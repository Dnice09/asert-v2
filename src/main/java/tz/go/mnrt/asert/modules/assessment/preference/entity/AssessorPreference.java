package tz.go.mnrt.asert.modules.assessment.preference.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;

import javax.persistence.*;

@Entity
@Table(name = "assessor_establishment_types")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssessorPreference extends BaseModel {

    @Column(name = "assessor_id", nullable = false)
    private Long assessorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessor_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    private Assessor assessor;

    @Enumerated(EnumType.STRING)
    @Column(name = "preference", nullable = false)
    private PropertyType preference;
}
