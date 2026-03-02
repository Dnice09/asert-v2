package tz.go.mnrt.asert.modules.company.dtos;

import lombok.*;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.company.entity.Company;
import tz.go.mnrt.asert.modules.contactperson.dtos.ContactPersonRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class CompanyRequestDto implements Serializable {
    private Long id;
    private UUID uuid;

    @NotNull
    private String name;

    private String tradingName;

    @NotNull
    private Long companyTypeId;

    private String website;

    @NotNull
    private String email;

    @NotNull
    private String tin;

    private String vat;

    @NotNull
    private String registrationType;

    @NotNull
    private String certificateRegistrationNumber;

    private LocalDate registrationDate;

    @NotNull
    private String registrationStatus;

    private String address;

    @NotNull
    private Boolean isActive;

    @Valid
    private List<ContactPersonRequestDto> contactPersons;

    public CompanyRequestDto(Company company) {
        BeanUtils.copyProperties(company, this);
    }
}
