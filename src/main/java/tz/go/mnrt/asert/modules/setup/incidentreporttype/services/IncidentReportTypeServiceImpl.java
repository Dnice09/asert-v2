package tz.go.mnrt.asert.modules.setup.incidentreporttype.services;

import java.util.Map;
import java.util.UUID;

import javax.validation.ValidationException;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.setup.incidentreporttype.dtos.IncidentReportTypeRequestDto;
import tz.go.mnrt.asert.modules.setup.incidentreporttype.dtos.IncidentReportTypeResponseDto;
import tz.go.mnrt.asert.modules.setup.incidentreporttype.entity.IncidentReportType;
import tz.go.mnrt.asert.modules.setup.incidentreporttype.repository.IncidentReportTypeRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class IncidentReportTypeServiceImpl extends SimpleSearchService<IncidentReportType> implements IncidentReportTypeService {
  private final IncidentReportTypeRepository incidentReportTypeRepository;

  @Override
  public IncidentReportTypeRequestDto save(IncidentReportTypeRequestDto incidentReportTypeRequestDto) {
    IncidentReportType incidentReportType = new IncidentReportType();
    if (incidentReportTypeRequestDto.getUuid() != null) {
      incidentReportType =
          incidentReportTypeRepository
              .findByUuid(incidentReportTypeRequestDto.getUuid())
              .orElseThrow(
                  () ->
                      new ValidationException(
                          "IncidentReportType with uuid {" + incidentReportTypeRequestDto.getUuid() + "} not found"));
    }
    BeanUtils.copyProperties(incidentReportTypeRequestDto, incidentReportType, "uuid");
    assert (incidentReportType.getUuid() != null);
    incidentReportType = incidentReportTypeRepository.save(incidentReportType);
    incidentReportTypeRequestDto.setId(incidentReportType.getId());
    return incidentReportTypeRequestDto;
  }

  @Override
  public Page<IncidentReportTypeResponseDto> findAll(Pageable page, Map<String, String> search) {
    log.info("Loading paginated IncidentReportTypes with page {} and search {} ", page, search);
    return incidentReportTypeRepository
        .findAll(createSpecification(IncidentReportType.class, search), page)
        .map(IncidentReportTypeResponseDto::new);
  }

  @Override
  public IncidentReportTypeResponseDto findByUuid(UUID uuid) {
    log.info("finding role with uuid {} ", uuid);
    return incidentReportTypeRepository
        .findByUuid(uuid)
        .map(IncidentReportTypeResponseDto::new)
        .orElseThrow(() -> new ValidationException("IncidentReportType with uuid {" + uuid + "} not found"));
  }

  @Override
  public void delete(UUID uuid) {
    log.info("deleting IncidentReportType with uuid {} ", uuid);
    incidentReportTypeRepository.softDelete(uuid);
  }
}
