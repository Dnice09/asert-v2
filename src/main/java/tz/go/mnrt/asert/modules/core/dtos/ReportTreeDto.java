package tz.go.mnrt.asert.modules.core.dtos;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReportTreeDto implements Serializable {
    private Long id;
    private String url;
    private String name;
    private List<ReportTreeDto> children;
    private Long parentId;
}
