package tz.go.mnrt.asert.modules.adminhierarchy.dtos;

import org.springframework.beans.factory.annotation.Value;

import java.util.Set;
import java.util.UUID;

public interface AdminHierarchyTreeDto {
    Long getId();

    UUID getUuid();

    String getName();

    String getCode();

    @Value("#{target.adminHierarchyLevel?.id}")
    Long getLevelId();

    @Value("#{target.adminHierarchyLevel?.code}")
    String getLevelCode();

    @Value("#{target.adminHierarchyLevel?.name}")
    String getLevelName();

    @Value("#{target.adminHierarchyLevel?.position}")
    String getLevelPosition();

    Set<ChildrenDto> getChildren();

    interface ChildrenDto {
        Long getId();

        UUID getUuid();

        String getName();

        String getCode();

        @Value("#{target.adminHierarchyLevel?.id}")
        Long getLevelId();

        @Value("#{target.adminHierarchyLevel?.code}")
        String getLevelCode();

        @Value("#{target.adminHierarchyLevel?.name}")
        String getLevelName();

        @Value("#{target.adminHierarchyLevel?.position}")
        String getLevelPosition();

        // add for recursive structure
        Set<ChildrenDto> getChildren();
    }

}
