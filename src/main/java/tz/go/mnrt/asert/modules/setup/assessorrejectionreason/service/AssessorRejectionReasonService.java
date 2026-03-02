package tz.go.mnrt.asert.modules.setup.assessorrejectionreason.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.setup.assessorrejectionreason.dto.AssessorRejectionReasonDto;

import java.util.Map;
import java.util.UUID;

@Service
public interface AssessorRejectionReasonService {

  AssessorRejectionReasonDto save(AssessorRejectionReasonDto assessorRejectionReasonDto);

  Page<AssessorRejectionReasonDto> findAll(Pageable page, Map<String, String> search);

  AssessorRejectionReasonDto findByUuid(UUID id);

  void delete(UUID uuid);
}
