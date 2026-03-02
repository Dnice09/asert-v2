package tz.go.mnrt.asert.modules.setup.country.dtos;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.setup.country.entity.Country;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CountryResponseDto {
  private Long id;
  private UUID uuid;

  @NotNull(message = "Created Date is required")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDateTime createdDate;

  private String name;

  private String countryCode;

  private String zipCode;

  public CountryResponseDto(Country entity) {
    entity.toDao(this);
  }
}
