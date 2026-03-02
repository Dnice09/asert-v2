package tz.go.mnrt.asert.modules.setup.documenttype.entity;

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
@Table(name = "document_types")
public class DocumentType extends BaseModel {
    @Column(name = "name", nullable = false, unique = true)
    private String name;
}
