package tz.go.mnrt.asert.modules.apikey.dtos;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeyBulkPublishDto {
    private List<ApiKeyPublishDto> publishedKeys;
    private int count;
}
