package tz.go.mnrt.asert.modules.assessment.certification.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.assessment.certification.dto.AssessorCertificationDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public interface AssessorCertificationService {

    AssessorCertificationDto save(AssessorCertificationDto assessorCertificationDto);

    Page<AssessorCertificationDto> findAll(Pageable page, Map<String, String> search);

    List<AssessorCertificationDto> findAll(Long assessorId);

    AssessorCertificationDto findByUuid(UUID id);

    void delete(UUID uuid);
}
