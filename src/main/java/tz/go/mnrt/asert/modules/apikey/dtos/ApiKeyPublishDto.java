package tz.go.mnrt.asert.modules.apikey.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.apikey.entity.ApiKey;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeyPublishDto {

    private String systemName;

    private String code;

    private String apiKey;

    private String systemIp;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiryDate;

    public ApiKeyPublishDto(ApiKey entity) {
        entity.toDao(this);
    }
}
