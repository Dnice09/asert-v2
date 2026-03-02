package tz.go.mnrt.asert.modules.apikey.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApiKeyStatusDto {
    private Boolean isRetired;
    private Boolean isApproved;
    private UUID apiKeyUuid;
}
