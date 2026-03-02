package tz.go.mnrt.asert.modules.adminhierarchy.dtos;

import lombok.Getter;
import lombok.Setter;
import tz.go.mnrt.asert.modules.adminhierarchy.entity.AdminHierarchy;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class AdminHierarchyTreeDtoImpl implements AdminHierarchyTreeDto {
    private Long id;
    private UUID uuid;
    private String name;
    private String code;
    private Long levelId;
    private String levelCode;
    private String levelName;
    private String levelPosition;
    private Set<ChildrenDto> children;
    private boolean highlighted = false;
    private boolean expanded = false;
    private boolean focused = false;

    public AdminHierarchyTreeDtoImpl(AdminHierarchy entity) {
        this.id = entity.getId();
        this.uuid = entity.getUuid();
        this.name = entity.getName();
        this.code = entity.getCode();
        this.levelId = entity.getLevelId();
        this.levelCode = entity.getAdminHierarchyLevel() != null ? entity.getAdminHierarchyLevel().getCode() : null;
        this.levelName = entity.getAdminHierarchyLevel() != null ? entity.getAdminHierarchyLevel().getName() : null;
        this.levelPosition = entity.getAdminHierarchyLevel() != null
                ? String.valueOf(entity.getAdminHierarchyLevel().getPosition())
                : null;
        this.children = entity.getChildren() != null
                ? entity.getChildren().stream()
                        .map(ChildrenDtoImpl::new)
                        .collect(Collectors.toSet())
                : Set.of();
    }

    @Getter
    @Setter
    public static class ChildrenDtoImpl implements ChildrenDto {
        private Long id;
        private UUID uuid;
        private String name;
        private String code;
        private Long levelId;
        private String levelCode;
        private String levelName;
        private String levelPosition;
        private Set<ChildrenDto> children;
        private boolean highlighted = false;
        private boolean expanded = false;
        private boolean focused = false;

        public ChildrenDtoImpl(AdminHierarchy entity) {
            this.id = entity.getId();
            this.uuid = entity.getUuid();
            this.name = entity.getName();
            this.code = entity.getCode();
            this.levelId = entity.getLevelId();
            this.levelCode = entity.getAdminHierarchyLevel() != null ? entity.getAdminHierarchyLevel().getCode() : null;
            this.levelName = entity.getAdminHierarchyLevel() != null ? entity.getAdminHierarchyLevel().getName() : null;
            this.levelPosition = entity.getAdminHierarchyLevel() != null
                    ? String.valueOf(entity.getAdminHierarchyLevel().getPosition())
                    : null;
            this.children = entity.getChildren() != null
                    ? entity.getChildren().stream()
                            .map(ChildrenDtoImpl::new)
                            .collect(Collectors.toSet())
                    : Set.of();
        }
    }
}
