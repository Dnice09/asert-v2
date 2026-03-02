package tz.go.mnrt.asert.modules.bednight.visitor.dtos;

import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.bednight.visitor.entity.Visitor;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VisitorRequestDto {
  private Long id;
  private UUID uuid;

  @NotNull(message = "full_name is required")
  private String fullName;

  private Integer countryId;

  private LocalDate dateOfBirth;

  @Size(max = 6, message = "gender cannot exceed 6 characters")
  private String gender;

  private String placeOfBirth;

  private String permanentPhysicalAddress;

  @Size(max = 15, message = "mobile_number cannot exceed 15 characters")
  private String mobileNumber;

  private String email;

  private Integer identificationTypeId;

  private String idNumber;

  private Integer userId;

  private Integer companyId;

  private String passportBorder;

  private String docType;

  private String docEntryPoint;

  private LocalDateTime docEntryDate;

  private LocalDateTime docExitDate;

  private String docResidencePlace;

  private String visaType;

  private String visaNumber;

  private String visaVisitPurpose;

  public VisitorRequestDto(Visitor entity) {
    entity.toDao(this);
  }
}
