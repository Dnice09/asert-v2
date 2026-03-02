package tz.go.mnrt.asert.modules.setup.institute.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.setup.institute.dto.InstituteDto;
import tz.go.mnrt.asert.modules.setup.institute.entity.Institute;
import tz.go.mnrt.asert.modules.setup.institute.repository.InstituteRepository;
import tz.go.mnrt.asert.modules.setup.institute.repository.InstituteSpecification;
import tz.go.mnrt.asert.modules.setup.institute.service.InstituteService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstituteServiceImpl implements InstituteService {
    private final InstituteRepository instituteRepository;

    @Override
    public InstituteDto save(InstituteDto dto) {
        Institute entity;
        if (dto.getUuid() != null) {
            // Update mode
            entity = instituteRepository.findByUuid(dto.getUuid())
                .orElseThrow(() -> new ValidationException("Institute not found."));
        } else {
            // New mode
            entity = new Institute();
            entity.setUuid(UUID.randomUUID());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setDeleted(false);
            // Optionally: setCreatedBy from security context
        }

        entity.setName(dto.getName());
        entity.setCountryId(dto.getCountryId());
        entity.setUpdatedAt(LocalDateTime.now());
        // Optionally: setUpdatedBy

        instituteRepository.save(entity);

        InstituteDto response = new InstituteDto();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    @Override
    public Page<InstituteDto> findAll(Pageable page, Map<String, String> search) {
        String filter = "";
        Long countryId = null;
        if (search != null && !search.isEmpty()) {
            filter = search.get("search");
            String countryIdStr = search.get("countryId");
            if(countryIdStr != null && !countryIdStr.isEmpty()){
                countryId = Long.valueOf(countryIdStr);
            }
        }

        Specification<Institute> spec = Specification.where(InstituteSpecification.notDeleted());

        if (filter != null && !filter.isBlank()) {
            spec = spec.and(InstituteSpecification.search(filter));
        }

        if (countryId != null && countryId > 0) {
            spec = spec.and(InstituteSpecification.byCountry(countryId));
        }

        Page<Institute> results = instituteRepository.findAll(spec, page);

        return results.map(InstituteDto::new);
    }


    @Override
    public InstituteDto findByUuid(UUID uuid) {
        Institute entity = instituteRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Institute not found."));
        InstituteDto dto = new InstituteDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public void delete(UUID uuid) {
        Institute entity = instituteRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Institute not found."));
        entity.setDeleted(true);
        entity.setUpdatedAt(LocalDateTime.now());
        instituteRepository.save(entity);
    }
}

