package tz.go.mnrt.asert.modules.setup.educationlevel.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.setup.educationlevel.dto.EducationLevelDto;

import java.util.Map;
import java.util.UUID;

@Service
public interface EducationLevelService {

  EducationLevelDto save(EducationLevelDto equipmentCategoryDto);

  Page<EducationLevelDto> findAll(Pageable page, Map<String, String> search);

  EducationLevelDto findByUuid(UUID id);

  void delete(UUID uuid);
}
