package tz.go.mnrt.asert.modules.setup.country.entity;

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
@Table(name = "countries")
public class Country extends BaseModel {

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "country_code", nullable = false, length = 50)
    private String countryCode;

    @Column(name = "zip_code", length = 50)
    private String zipCode;
}
