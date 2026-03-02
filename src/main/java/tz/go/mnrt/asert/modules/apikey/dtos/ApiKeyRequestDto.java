package tz.go.mnrt.asert.modules.apikey.dtos;

import lombok.*;
import tz.go.mnrt.asert.modules.apikey.entity.ApiKey;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeyRequestDto {
    private Long id;

    private UUID uuid;

    private String systemName;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiryDate;

    private String status;

    private String code;

    private String apiKey;

    private Boolean isApproved;

    private Boolean isRetired;

    private String contactEmail;

    private String systemIp;

    public ApiKeyRequestDto(ApiKey entity) {
        entity.toDao(this);
    }
}
