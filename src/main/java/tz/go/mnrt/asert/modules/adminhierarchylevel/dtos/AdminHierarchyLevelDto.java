package tz.go.mnrt.asert.modules.adminhierarchylevel.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.adminhierarchylevel.entity.AdminHierarchyLevel;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminHierarchyLevelDto implements Serializable {
  private Long id;
  private UUID uuid;
  @NotNull private String name;
  private String code;
  @NotNull private Integer position;

  public AdminHierarchyLevelDto(AdminHierarchyLevel level) {
    BeanUtils.copyProperties(level, this);
  }
}
