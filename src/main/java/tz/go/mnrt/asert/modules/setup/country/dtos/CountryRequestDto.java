package tz.go.mnrt.asert.modules.setup.country.dtos;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
public class CountryRequestDto {
  private Long id;
  private UUID uuid;

  @NotNull(message = "name is required")
  @Size(max = 50, message = "name cannot exceed 50 characters")
  private String name;

  @NotNull(message = "countryCode is required")
  @Size(max = 50, message = "countryCode cannot exceed 50 characters")
  private String countryCode;

  @Size(max = 50, message = "zipCode cannot exceed 50 characters")
  private String zipCode;

  public CountryRequestDto(Country entity) {
    entity.toDao(this);
  }
}
