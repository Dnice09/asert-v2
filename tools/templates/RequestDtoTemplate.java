package tz.go.mnrt.asert.modules.#package_name.dtos;

import java.util.UUID;

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
public class #module_nameRequestDto {
  private Long id;
  private UUID uuid;

  public #module_nameRequestDto(#module_name entity) {
    entity.toDao(this);
  }
}
