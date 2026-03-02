package tz.go.mnrt.asert.modules.setup.attachmenttype.entity;

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
@Table(name = "attachment_types")
public class AttachmentType extends BaseModel {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired;

    @Column(name = "is_active", nullable = false, unique = true)
    private Boolean isActive;

    /*@OneToMany(mappedBy = "attachmentType")
    @JsonIgnore
    private Set<FacilityAttachment> facilityAttachments;*/
}
