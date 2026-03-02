package tz.go.mnrt.asert.modules.setup.identificationtype.dtos;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.setup.identificationtype.entity.IdentificationType;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class IdentificationTypeRequestDto {
  private Long id;
  private UUID uuid;

  @NotNull(message = "name is required")
  private String name;

  @Size(max = 50, message = "code cannot exceed 50 characters")
  private String code;

  private String issuer;

  public IdentificationTypeRequestDto(IdentificationType entity) {
    entity.toDao(this);
  }
}
