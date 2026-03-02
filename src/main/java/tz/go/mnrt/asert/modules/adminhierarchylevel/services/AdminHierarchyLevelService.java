package tz.go.mnrt.asert.modules.adminhierarchylevel.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.go.mnrt.asert.modules.adminhierarchylevel.dtos.AdminHierarchyLevelDto;
import tz.go.mnrt.asert.modules.adminhierarchylevel.entity.AdminHierarchyLevel;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface AdminHierarchyLevelService {

    AdminHierarchyLevelDto save(AdminHierarchyLevelDto levelDto);

    Page<AdminHierarchyLevelDto> findAll(Pageable page, Map<String, String> search);

    AdminHierarchyLevelDto findByUuid(UUID id);

    void delete(UUID uuid);

    boolean exists(String code, int position);

    Optional<AdminHierarchyLevel> findByCode(String code);
}
