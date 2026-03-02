package tz.go.mnrt.asert.modules.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoggedInUserDto {
  private Long id;
  private UUID uuid;
  private String email;
  private String firstName;
  private String lastName;
  private Long adminHierarchyId;
}
