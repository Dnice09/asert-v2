package tz.go.mnrt.asert.modules.assessment.preference.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.assessment.preference.dto.AssessorPreferenceDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public interface AssessorPreferenceService {

    AssessorPreferenceDto save(AssessorPreferenceDto assessorReferenceDto);

    Page<AssessorPreferenceDto> findAll(Pageable page, Map<String, String> search);

    List<AssessorPreferenceDto> findAll(Long assessorId);

    AssessorPreferenceDto findByUuid(UUID id);

    void delete(UUID uuid);
}
