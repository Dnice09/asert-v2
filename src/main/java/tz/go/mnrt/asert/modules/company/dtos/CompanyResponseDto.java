package tz.go.mnrt.asert.modules.company.dtos;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.company.entity.Company;
import tz.go.mnrt.asert.modules.contactperson.dtos.ContactPersonResponseDto;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CompanyResponseDto implements Serializable {
    private Long id;
    private UUID uuid;

    @NotNull
    private String name;

    private String tradingName;

    @NotNull
    private Long companyTypeId;

    private String companyTypeName;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate registrationDate;

    @NotNull
    private String registrationStatus;

    private String address;

    @NotNull
    private Boolean isActive;

    private List<ContactPersonResponseDto> contactPersons;

    @NotNull(message = "Created Date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    public CompanyResponseDto(Company company) {
        BeanUtils.copyProperties(company, this);
        // Rename createdDate to match the field in the entity
        this.createdAt = company.getCreatedAt();
        // Set companyTypeName if companyType is available
        if (company.getCompanyType() != null) {
            this.companyTypeName = company.getCompanyType().getName();
        }
    }
}
