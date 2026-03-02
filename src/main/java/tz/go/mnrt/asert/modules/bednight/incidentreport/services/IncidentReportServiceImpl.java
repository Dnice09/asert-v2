package tz.go.mnrt.asert.modules.bednight.incidentreport.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.bednight.incidentreport.dtos.IncidentReportRequestDto;
import tz.go.mnrt.asert.modules.bednight.incidentreport.dtos.IncidentReportResponseDto;
import tz.go.mnrt.asert.modules.bednight.incidentreport.entity.IncidentReport;
import tz.go.mnrt.asert.modules.bednight.incidentreport.repository.IncidentReportRepository;


@Service
@Slf4j
@RequiredArgsConstructor
public class IncidentReportServiceImpl extends SimpleSearchService<IncidentReport> implements IncidentReportService {
  private final IncidentReportRepository incidentReportRepository;

  @Override
  public IncidentReportRequestDto save(IncidentReportRequestDto incidentReportRequestDto) {
    IncidentReport incidentReport = new IncidentReport();
    if (incidentReportRequestDto.getUuid() != null) {
      incidentReport =
          incidentReportRepository
              .findByUuid(incidentReportRequestDto.getUuid())
              .orElseThrow(
                  () ->
                      new ValidationException(
                          "IncidentReport with uuid {" + incidentReportRequestDto.getUuid() + "} not found"));
    }
    BeanUtils.copyProperties(incidentReportRequestDto, incidentReport, "uuid");
    assert (incidentReport.getUuid() != null);
    incidentReport = incidentReportRepository.save(incidentReport);
    incidentReportRequestDto.setId(incidentReport.getId());
    return incidentReportRequestDto;
  }

  @Override
  public Page<IncidentReportResponseDto> findAll(Pageable page, Map<String, String> search) {
    log.info("Loading paginated IncidentReports with page {} and search {} ", page, search);
    return incidentReportRepository
        .findAll(createSpecification(IncidentReport.class, search), page)
        .map(IncidentReportResponseDto::new);
  }

  @Override
  public IncidentReportResponseDto findByUuid(UUID uuid) {
    log.info("finding role with uuid {} ", uuid);
     return incidentReportRepository
        .findByUuid(uuid)
        .map(IncidentReportResponseDto::new)
        .orElseThrow(() -> new ValidationException("IncidentReport with uuid {" + uuid + "} not found"));
  }

  @Override
  public void delete(UUID uuid) {
    log.info("deleting IncidentReport with uuid {} ", uuid);
    incidentReportRepository.softDelete(uuid);
  }

  @Override
  public List<IncidentReportResponseDto> findByVisitorUuid(UUID id) {
      return incidentReportRepository.findByVisitorUuid(id).stream()
      .map(IncidentReportResponseDto::new)
      .collect(Collectors.toList());
  }
}
