package tz.go.mnrt.asert.modules.setup.companytype.dtos;

import java.io.Serializable;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.*;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.setup.companytype.entity.CompanyType;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class CompanyTypeRequestDto implements Serializable {
    private Long id;

    private UUID uuid;

    @NotNull
    private String name;

    @NotNull
    private String code;

    @NotNull
    private Boolean isActive;

    public CompanyTypeRequestDto(CompanyType companyType) {
        BeanUtils.copyProperties(companyType, this);
    }
}
