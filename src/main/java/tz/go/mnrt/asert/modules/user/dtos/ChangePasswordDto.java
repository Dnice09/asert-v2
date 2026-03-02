package tz.go.mnrt.asert.modules.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDto {

//  @NotNull private UUID userUuid;
  @NotNull private String newPassword;
}
