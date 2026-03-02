package tz.go.mnrt.asert.modules.setup.identificationtype.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "identification_types")
public class IdentificationType extends BaseModel {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "issuer")
    private String issuer;
}
