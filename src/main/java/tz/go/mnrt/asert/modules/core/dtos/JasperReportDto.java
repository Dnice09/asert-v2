package tz.go.mnrt.asert.modules.core.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JasperReportDto {
    private String name;
    private Map<String, String> params;
    private String format;
}
