package tz.go.mnrt.asert.modules.setup.educationlevel.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.setup.educationlevel.dto.EducationLevelDto;
import tz.go.mnrt.asert.modules.setup.educationlevel.entity.EducationLevel;
import tz.go.mnrt.asert.modules.setup.educationlevel.repository.EducationLevelRepository;
import tz.go.mnrt.asert.modules.setup.educationlevel.repository.EducationLevelSpecification;
import tz.go.mnrt.asert.modules.setup.educationlevel.service.EducationLevelService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EducationLevelServiceImpl implements EducationLevelService {
    private final EducationLevelRepository educationLevelRepository;

    @Override
    public EducationLevelDto save(EducationLevelDto dto) {
        // Check for duplicate name
        educationLevelRepository.findByNameIgnoreCase(dto.getName()).ifPresent(existing -> {
            if (dto.getUuid() == null || !existing.getUuid().equals(dto.getUuid())) {
                throw new ValidationException("Education Level with the same name already exists.");
            }
        });

        EducationLevel entity;
        if (dto.getUuid() != null) {
            // Update mode
            entity = educationLevelRepository.findByUuid(dto.getUuid())
                .orElseThrow(() -> new ValidationException("Education Level not found."));
        } else {
            // New mode
            entity = new EducationLevel();
            entity.setUuid(UUID.randomUUID());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setDeleted(false);
            // Optionally: setCreatedBy from security context
        }

        entity.setName(dto.getName());
        entity.setUpdatedAt(LocalDateTime.now());
        // Optionally: setUpdatedBy

        educationLevelRepository.save(entity);

        EducationLevelDto response = new EducationLevelDto();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    @Override
    public Page<EducationLevelDto> findAll(Pageable page, Map<String, String> search) {
        String filter = "";
        if (search != null && !search.isEmpty()) {
            filter = search.get("search");
        }

        Specification<EducationLevel> spec = Specification.where(EducationLevelSpecification.notDeleted());

        if (filter != null && !filter.isBlank()) {
            spec = spec.and(EducationLevelSpecification.search(filter));
        }

        Page<EducationLevel> results = educationLevelRepository.findAll(spec, page);

        return results.map(entity -> {
            EducationLevelDto dto = new EducationLevelDto();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        });
    }


    @Override
    public EducationLevelDto findByUuid(UUID uuid) {
        EducationLevel entity = educationLevelRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Education Level not found."));
        EducationLevelDto dto = new EducationLevelDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public void delete(UUID uuid) {
        EducationLevel entity = educationLevelRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Education Level not found."));
        entity.setDeleted(true);
        entity.setUpdatedAt(LocalDateTime.now());
        // Optionally: setUpdatedBy
        educationLevelRepository.save(entity);
    }
}

