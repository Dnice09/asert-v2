package tz.go.mnrt.asert.modules.assessment.educationbackground.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.assessment.educationbackground.dto.EducationBackgroundDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public interface EducationBackgroundService {

    EducationBackgroundDto save(EducationBackgroundDto educationBackgroundDto);

    Page<EducationBackgroundDto> findAll(Pageable page, Map<String, String> search);

    List<EducationBackgroundDto> findAll(Long assessorId);

    EducationBackgroundDto findByUuid(UUID id);

    void delete(UUID uuid);
}
