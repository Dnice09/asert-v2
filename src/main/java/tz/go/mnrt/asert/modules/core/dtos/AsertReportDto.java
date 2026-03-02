package tz.go.mnrt.asert.modules.core.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.core.entities.Report;

import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class AsertReportDto {
    private Long id;

    private UUID uuid;
    private String name;
    private String url;
    private Long parentId;
    private String parentName;
    private Set<AsertReportDto> children;

    public AsertReportDto(Report asertReport) {
        BeanUtils.copyProperties(asertReport, this);
        if (asertReport.getParent() != null) {
            this.parentId = asertReport.getParent().getId();
            this.parentName = asertReport.getParent().getName();
        }
    }
}
