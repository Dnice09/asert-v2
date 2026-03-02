package tz.go.mnrt.asert.modules.setup.institute.dto;

import lombok.*;
import tz.go.mnrt.asert.modules.setup.institute.entity.Institute;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class InstituteDto implements Serializable {
    private Long id;
    private UUID uuid;
    private String name;
    private Long countryId;
    private String countryName;

    public InstituteDto(Institute entity) {
        entity.toDao(this);
        this.countryId = entity.getCountry().getId();
        this.countryName = entity.getCountry().getName();
    }
}
