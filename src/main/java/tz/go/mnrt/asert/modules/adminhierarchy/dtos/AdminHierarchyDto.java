package tz.go.mnrt.asert.modules.adminhierarchy.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.adminhierarchy.entity.AdminHierarchy;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminHierarchyDto implements Serializable {
    private Long id;

    private UUID uuid;

    @NotNull
    private String name;
    private String code;
    private String isoCode;
    private Long parentId;
    private String parentName;

    @NotNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long adminHierarchyLevelId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String adminHierarchyLevelName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long levelId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String levelName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String levelCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer levelPosition;

    public AdminHierarchyDto(
        Long id,
        UUID uuid,
        String name,
        String code,
        Long levelId,
        String levelCode,
        String levelName,
        Integer levelPosition) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.code = code;
        this.levelId = levelId;
        this.levelCode = levelCode;
        this.levelName = levelName;
        this.levelPosition = levelPosition;
    }

    public AdminHierarchyDto(AdminHierarchy hierarchy) {
        BeanUtils.copyProperties(hierarchy, this);
        adminHierarchyLevelId = hierarchy.getAdminHierarchyLevel().getId();
        adminHierarchyLevelName = hierarchy.getAdminHierarchyLevel().getName();
        levelCode = hierarchy.getAdminHierarchyLevel().getCode();
        if (hierarchy.getParent() != null) {
            parentId = hierarchy.getParent().getId();
            parentName = hierarchy.getParent().getName();
        }
    }
}
