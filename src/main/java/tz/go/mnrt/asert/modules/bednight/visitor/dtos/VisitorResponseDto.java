package tz.go.mnrt.asert.modules.bednight.visitor.dtos;

import java.time.LocalDateTime;
import java.util.UUID;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.bednight.visitor.entity.Visitor;
import tz.go.mnrt.asert.modules.setup.country.dtos.CountryResponseDto;
import tz.go.mnrt.asert.modules.setup.identificationtype.dtos.IdentificationTypeResponseDto;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VisitorResponseDto {
  private Long id;
  private UUID uuid;

  @NotNull(message = "Created Date is required")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDateTime createdDate;

    private String fullName;

    private String name;

  private Integer countryId;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dateOfBirth;

  private String gender;

  private String placeOfBirth;

  private String permanentPhysicalAddress;

  private String mobileNumber;

  private String email;

  private Integer identificationTypeId;

  private String idNumber;

  private Integer userId;

  private Integer companyId;

  private String passportBorder;

  private String docType;

  private String docEntryPoint;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime docEntryDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime docExitDate;

  private String docResidencePlace;

  private String visaType;

  private String visaNumber;

  private String visaVisitPurpose;

  private IdentificationTypeResponseDto identificationType = new IdentificationTypeResponseDto();

  private CountryResponseDto country = new CountryResponseDto();

  private String identificationTypeName;

  private String countryName;

  public VisitorResponseDto(Visitor visitor) {
      BeanUtils.copyProperties(visitor,this);
      if(visitor.getIdentificationType() != null){
        BeanUtils.copyProperties(visitor.getIdentificationType(), this.identificationType);
        this.identificationTypeName = visitor.getIdentificationType().getName();
      }
      if(visitor.getCountry() != null){
        BeanUtils.copyProperties(visitor.getCountry(), this.country);
        this.countryName = visitor.getCountry().getName();
      }
      this.name = visitor.getFullName();
  }
}
