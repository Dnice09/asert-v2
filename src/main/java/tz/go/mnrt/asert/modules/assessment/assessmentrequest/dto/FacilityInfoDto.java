package tz.go.mnrt.asert.modules.assessment.assessmentrequest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityInfoDto {

    @NotBlank(message = "Facility name is required")
    @Size(max = 255, message = "Facility name must not exceed 255 characters")
    private String facilityName;

    @NotBlank(message = "Facility type is required")
    @Size(max = 100, message = "Facility type must not exceed 100 characters")
    private String facilityType;

    @NotBlank(message = "Contact person is required")
    @Size(max = 255, message = "Contact person must not exceed 255 characters")
    private String contactPerson;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    @Size(max = 1000, message = "Address must not exceed 1000 characters")
    private String address;

    @NotNull(message = "Requested date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate requestedDate;
}