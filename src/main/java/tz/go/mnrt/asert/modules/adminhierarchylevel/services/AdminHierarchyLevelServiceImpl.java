package tz.go.mnrt.asert.modules.adminhierarchylevel.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.adminhierarchylevel.entity.AdminHierarchyLevel;
import tz.go.mnrt.asert.modules.adminhierarchylevel.repository.AdminHierarchyLevelRepository;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.adminhierarchylevel.dtos.AdminHierarchyLevelDto;

import javax.validation.ValidationException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminHierarchyLevelServiceImpl extends SimpleSearchService<AdminHierarchyLevel>
    implements AdminHierarchyLevelService {

    private final AdminHierarchyLevelRepository levelRepository;

    @Override
    public AdminHierarchyLevelDto save(AdminHierarchyLevelDto levelDto) {

        AdminHierarchyLevel level = new AdminHierarchyLevel();
        if (levelDto.getUuid() != null) {
            level =
                levelRepository
                    .findByUuid(levelDto.getUuid())
                    .orElseThrow(
                        () ->
                            new ValidationException(
                                "Admin level with uuid {" + levelDto.getUuid() + "} not found"));
        }
        BeanUtils.copyProperties(levelDto, level, "uuid");
        level = levelRepository.save(level);
        levelDto.setId(level.getId());
        return levelDto;
    }

    @Override
    public Page<AdminHierarchyLevelDto> findAll(Pageable page, Map<String, String> search) {
        return levelRepository
            .findAll(createSpecification(AdminHierarchyLevel.class, search), page)
            .map(AdminHierarchyLevelDto::new);
    }

    @Override
    public AdminHierarchyLevelDto findByUuid(UUID uuid) {
        return levelRepository
            .findByUuid(uuid)
            .map(AdminHierarchyLevelDto::new)
            .orElseThrow(
                () -> new ValidationException("Admin level with uuid {" + uuid + "} not found"));
    }

    @Override
    public void delete(UUID uuid) {
        levelRepository.deleteByUuid(uuid);
    }

    @Override
    public boolean exists(String code, int position) {
        return levelRepository.existsByCodeIgnoreCase(code) ||
            levelRepository.existsByPosition(position);
    }

    @Override
    public Optional<AdminHierarchyLevel> findByCode(String code) {
        return levelRepository.findByCode(code);
    }
}
