package tz.go.mnrt.asert.modules.company.dtos;

import java.io.Serializable;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.company.entity.Company;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class CompanyInfoDto implements Serializable {
    private Long id;
    private UUID uuid;

    @NotNull
    private String name;

    private String tradingName;

    public CompanyInfoDto(Company company) {
        BeanUtils.copyProperties(company, this);
    }

    public CompanyInfoDto getComanyInfo(Company company) {
        CompanyInfoDto companyInfoDto = new CompanyInfoDto();
        BeanUtils.copyProperties(company, companyInfoDto);
        return companyInfoDto;
    }
}
