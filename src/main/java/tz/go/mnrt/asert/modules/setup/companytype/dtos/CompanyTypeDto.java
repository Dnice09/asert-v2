package tz.go.mnrt.asert.modules.setup.companytype.dtos;

import java.io.Serializable;
import java.util.UUID;
import javax.validation.constraints.NotNull;

import lombok.*;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.setup.companytype.entity.CompanyType;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CompanyTypeDto implements Serializable {

    private Long id;

    private UUID uuid;

    @NotNull
    private String name;

    @NotNull
    private String code;

    @NotNull
    private Boolean isActive;

    public CompanyTypeDto(CompanyType companyType) {
        BeanUtils.copyProperties(companyType, this);
    }

    public CompanyTypeDto(String name, String code) {
        this.name = name;
        this.code = code;
    }

}
