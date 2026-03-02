package tz.go.mnrt.asert.modules.bednight.incidentreport.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.bednight.incidentreport.dtos.IncidentReportRequestDto;
import tz.go.mnrt.asert.modules.bednight.incidentreport.dtos.IncidentReportResponseDto;

public interface IncidentReportService {

  IncidentReportRequestDto save(IncidentReportRequestDto incidentReportDto);

  Page<IncidentReportResponseDto> findAll(Pageable page, Map<String, String> search);

  IncidentReportResponseDto findByUuid(UUID id);

  List<IncidentReportResponseDto> findByVisitorUuid(UUID id);

  void delete(UUID uuid);
}

