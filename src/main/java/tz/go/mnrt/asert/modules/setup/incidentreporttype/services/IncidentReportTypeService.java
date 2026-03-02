package tz.go.mnrt.asert.modules.setup.incidentreporttype.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.setup.incidentreporttype.dtos.IncidentReportTypeRequestDto;
import tz.go.mnrt.asert.modules.setup.incidentreporttype.dtos.IncidentReportTypeResponseDto;

public interface IncidentReportTypeService {

  IncidentReportTypeRequestDto save(IncidentReportTypeRequestDto incidentReportTypeDto);

  Page<IncidentReportTypeResponseDto> findAll(Pageable page, Map<String, String> search);

  IncidentReportTypeResponseDto findByUuid(UUID id);

  void delete(UUID uuid);
}

