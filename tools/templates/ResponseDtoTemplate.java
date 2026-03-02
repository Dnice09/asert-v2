package tz.go.mnrt.asert.modules.#package_name.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.#package_name.entity.#module_name;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class #module_nameResponseDto {
  private Long id;
  private UUID uuid;

  @NotNull(message = "Created Date is required")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDateTime createdDate;

  public #module_nameResponseDto(#module_name entity) {
    entity.toDao(this);
  }
}
